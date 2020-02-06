package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.AssetRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaService {

    private final InvestmentRepository investmentRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final AssetRepository assetRepository;

    public WalletHistory calculateWalletQuota(Wallet wallet) {
        WalletHistory walletHistory = walletHistoryRepository.findByWalletAndRegisterDate(wallet, LocalDate.now().minus(1, ChronoUnit.DAYS))
                .orElse(WalletHistory.builder().quota(BigDecimal.ONE).totalQuotas(BigDecimal.ZERO).build());

        List<Investment> investments = investmentRepository.findAllByWalletAndDate(wallet, LocalDate.now());
        BigDecimal investedTotalValue = investments.stream()
                .map(Investment::getInvestmentTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal actualValue = calculateWalletActualValue(wallet).subtract(investedTotalValue);
        BigDecimal newQuota = calculateQuota(actualValue, walletHistory.getWalletValue(), walletHistory.getQuota());
        WalletHistory todayWalletHistory = new WalletHistory(wallet.getId(),
                wallet, newQuota, walletHistory.getTotalQuotas(), LocalDate.now());
        todayWalletHistory.addTotalQuotas(investedTotalValue.divide(newQuota, 6, RoundingMode.CEILING));

        return todayWalletHistory;
    }

    private BigDecimal calculateQuota(BigDecimal actualValue, BigDecimal pastValue, BigDecimal pastQuota) {
        if (isEmpty(pastValue) || pastValue.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;

        BigDecimal dayVariation = calculatePercentage(pastValue, actualValue);
        BigDecimal newQuotaCalculated = pastQuota.add(pastQuota.multiply(dayVariation)).setScale(6, RoundingMode.CEILING);
        log.info("New Calculated Quota: {}", newQuotaCalculated);

        return newQuotaCalculated;
    }

    private BigDecimal calculatePercentage(BigDecimal firstValue, BigDecimal actualValue) {
        BigDecimal percentage = actualValue.subtract(firstValue).divide(firstValue, 8, RoundingMode.CEILING);
        log.info("Percentage founded: {}", percentage);

        return percentage;
    }

    private BigDecimal calculateWalletActualValue(Wallet wallet) {
        List<Asset> assets = assetRepository.findAssetsByWallet(wallet)
                .orElse(Collections.emptyList());
        BigDecimal actualValue = BigDecimal.ZERO;

        for (Asset asset : assets) {
            Long quantity = investmentRepository.getQuantityByWalletAndAssetAndDateBefore(wallet, asset)
                    .orElse(0L);

            actualValue = actualValue.add(assetHistoryRepository.findFirstByAssetOrderByDateDesc(asset)
                    .map(assetHistory -> assetHistory.getValue().multiply(BigDecimal.valueOf(quantity)))
                    .orElse(BigDecimal.ZERO));
        }

        return actualValue;
    }

    public WalletHistory recalculateWalletQuotaForDate(Wallet wallet, LocalDate dateToQuota) {
        WalletHistory walletHistory = walletHistoryRepository.findByWalletAndRegisterDate(wallet, dateToQuota.minus(1, ChronoUnit.DAYS))
                .orElse(WalletHistory.builder().quota(BigDecimal.ONE).totalQuotas(BigDecimal.ZERO).build());

        List<Investment> investments = investmentRepository.findAllByWalletAndDateBefore(wallet, dateToQuota);
        BigDecimal investedTotalValue = investments.stream()
                .map(Investment::getInvestmentTotal)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal actualValue = calculateWalletActualValueForDate(wallet, dateToQuota).subtract(investedTotalValue);
        BigDecimal newQuota = calculateQuota(actualValue, walletHistory.getWalletValue(), walletHistory.getQuota());
        WalletHistory todayWalletHistory = new WalletHistory(wallet.getId(),
                wallet, newQuota, walletHistory.getTotalQuotas(), dateToQuota);
        todayWalletHistory.addTotalQuotas(investedTotalValue.divide(newQuota, 6, RoundingMode.CEILING));

        return todayWalletHistory;
    }

    private BigDecimal calculateWalletActualValueForDate(Wallet wallet, LocalDate date) {
        List<Asset> assets = assetRepository.findInvestedAssetsByDate(wallet, date)
                .orElse(Collections.emptyList());
        BigDecimal actualValue = BigDecimal.ZERO;

        for (Asset asset : assets) {
            Long quantity = investmentRepository.getQuantityByWalletAndAssetAndDateBefore(wallet, asset, date)
                    .orElse(0L);

            actualValue = actualValue.add(assetHistoryRepository.findFirstByAssetAndDate(asset, date)
                    .map(assetHistory -> assetHistory.getValue().multiply(BigDecimal.valueOf(quantity)))
                    .orElse(BigDecimal.ZERO));
        }

        return actualValue;
    }

}
