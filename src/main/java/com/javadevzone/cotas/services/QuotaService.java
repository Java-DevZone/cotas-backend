package com.javadevzone.cotas.services;

import com.javadevzone.cotas.dto.QuotaHistory;
import com.javadevzone.cotas.dto.QuotaHistory.QuotaHistoryData;
import com.javadevzone.cotas.entity.*;
import com.javadevzone.cotas.exceptions.AssetHistoryNotFoundException;
import com.javadevzone.cotas.exceptions.AssetNotFoundException;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.AssetRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class QuotaService {

    private final InvestmentRepository investmentRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final AssetRepository assetRepository;

    public QuotaHistory calculateQuotaFor(Asset asset) {
        return investmentRepository.findAllByAssetOrderByDateAsc(asset)
                .flatMap(investments -> {
                    Optional<QuotaHistory> quotaHistory = calculateQuota(investments, asset);

                    return quotaHistory;
                })
                .orElseThrow(() -> new AssetNotFoundException(asset));
    }

    private Optional<QuotaHistory> calculateQuota(List<Investment> investments, Asset asset) {
        QuotaHistory quota = new QuotaHistory();
        quota.setTicket(asset.getTicket());
        quota.setDataList(new LinkedList<>());
        quota.setQuotaTotal(BigDecimal.ZERO);

        QuotaHistoryData lastHistoryData = null;
        for (Investment investment : investments) {
            BigDecimal newQuota = calculateQuota(investment.getInvestmentTotal(), lastHistoryData.getValue(), lastHistoryData.getQuota());

            lastHistoryData = new QuotaHistoryData(investment.getDate(), newQuota, investment.getInvestmentTotal());
            quota.getDataList().add(lastHistoryData);

            BigDecimal quotaQuantityForInvestment = investment.getInvestmentTotal().divide(lastHistoryData.getQuota(), 6, RoundingMode.CEILING);
            quota.addQuotaTotal(quotaQuantityForInvestment);
            quota.addQuantity(investment.getQuantity());
        }
        AssetHistory history = assetHistoryRepository.findFirstByAssetOrderByDateDesc(asset)
                .orElseThrow(() -> new AssetHistoryNotFoundException(asset));

        BigDecimal totalValue = history.getValue().multiply(BigDecimal.valueOf(quota.getQuantity()));
        BigDecimal newQuota = calculateQuota(totalValue, lastHistoryData.getValue(), lastHistoryData.getQuota());
        lastHistoryData = new QuotaHistoryData(history.getDate(), newQuota, totalValue);
        quota.getDataList().add(lastHistoryData);

        quota.setLastHistoryData(lastHistoryData);

        return Optional.of(quota);
    }

    private BigDecimal calculateQuota(BigDecimal actualValue, BigDecimal pastValue, BigDecimal pastQuota) {
        if (isEmpty(pastValue) || pastValue.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ONE;

        BigDecimal dayVariation = calculatePercentage(pastValue, actualValue);
        return pastQuota.add(pastQuota.multiply(dayVariation));
    }

    private BigDecimal calculatePercentage(BigDecimal firstValue, BigDecimal actualValue) {
        return actualValue.subtract(firstValue).divide(firstValue, 6, RoundingMode.CEILING);
    }

    public WalletHistory calculateWalletQuota(Wallet wallet) {
        List<Investment> investments = investmentRepository.findAllByWalletAndDate(wallet, LocalDate.now());
        BigDecimal actualValue = calculateWalletActualValue(wallet);

        WalletHistory walletHistory = walletHistoryRepository.findByWalletAndRegisterDate(wallet, LocalDate.now().minus(1, ChronoUnit.DAYS))
                .orElse(WalletHistory.builder().totalValue(BigDecimal.ZERO).quota(BigDecimal.ONE).totalQuotas(BigDecimal.ZERO).build());
        WalletHistory todayWalletHistory = walletHistory.toBuilder().build();

        for (Investment invest : investments) {
            BigDecimal newQuotasQuantity = invest.getInvestmentTotal().divide(todayWalletHistory.getQuota(), 6, RoundingMode.CEILING);
            todayWalletHistory.setTotalQuotas( newQuotasQuantity.add(todayWalletHistory.getTotalQuotas()) );
            todayWalletHistory.setTotalValue( todayWalletHistory.getWalletValue().add(invest.getInvestmentTotal()) );
        }

        BigDecimal newQuota = calculateQuota(actualValue, todayWalletHistory.getWalletValue(), todayWalletHistory.getQuota());
        todayWalletHistory.setQuota(newQuota);

        return walletHistoryRepository.save(todayWalletHistory);
    }

    private BigDecimal calculateWalletActualValue(Wallet wallet) {
        List<Asset> assets = assetRepository.findAssetsByWallet(wallet)
                .orElse(Collections.emptyList());
        BigDecimal actualValue = BigDecimal.ZERO;

        for (Asset asset : assets) {
            actualValue = actualValue.add(assetHistoryRepository.findFirstByAssetOrderByDateDesc(asset)
                    .map(assetHistory -> assetHistory.getValue().multiply(assetHistory.getQuantity()))
                    .orElse(BigDecimal.ZERO));
        }

        return actualValue;
    }

}
