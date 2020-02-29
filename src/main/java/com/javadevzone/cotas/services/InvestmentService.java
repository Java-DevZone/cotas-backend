package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.*;
import com.javadevzone.cotas.exceptions.WalletNotFoundException;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.AssetRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;
import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final AssetRepository assetRepository;
    private final InvestmentRepository investmentRepository;
    private final AssetHistoryRepository assetHistoryRepository;

    public BigDecimal calculateInvestmentsProfitability(WalletHistory walletHistory, LocalDate date) {
        BigDecimal totalWalletValue = assetRepository.findInvestedAssetsByDate(walletHistory.getWallet(), date)
                .orElse(emptyList())
                .stream()
                .map(asset -> {
                    Long quantity = investmentRepository.getQuantityByWalletAndAssetAndDateBefore(walletHistory.getWallet(), asset, date)
                            .orElse(0L);

                    AssetHistory assetHistory = assetHistoryRepository.findByAssetAndDate(asset, date)
                            .orElse(AssetHistory.builder().asset(asset).value(ZERO).build());
                    return assetHistory.getValue().multiply(new BigDecimal(quantity));
                })
                .reduce(BigDecimal::add)
                .orElse(ZERO);

        BigDecimal previousWalletValue = walletHistory.getWalletValue();

        if (previousWalletValue.compareTo(ZERO) == 0) {
            previousWalletValue = investmentRepository.findAllByWalletAndDateBefore(walletHistory.getWallet(), date)
                    .stream()
                    .map(Investment::getInvestmentTotal)
                    .reduce(BigDecimal::add)
                    .orElse(ZERO);
        }

        return calculatePercentage(previousWalletValue, totalWalletValue);
    }

    private BigDecimal calculatePercentage(BigDecimal firstValue, BigDecimal actualValue) {
        BigDecimal percentage = actualValue.subtract(firstValue).divide(firstValue, 8, CEILING);
        log.info("Percentage founded: {}", percentage);

        return percentage;
    }

    public Investment addInvestment(Investment investment) {
        if (Objects.isNull(investment.getWallet())) throw new WalletNotFoundException();

        investment.setDate(LocalDate.now());

        return investmentRepository.save(investment);
    }



}
