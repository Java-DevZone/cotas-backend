package com.javadevzone.cotas.services;

import com.javadevzone.cotas.dto.QuotaHistory;
import com.javadevzone.cotas.dto.QuotaHistory.QuotaHistoryData;
import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.exceptions.AssetHistoryNotFoundException;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class QuotaService {

    private final InvestmentRepository investmentRepository;
    private final AssetHistoryRepository assetHistoryRepository;

    public BigDecimal calculateQuotaFor(Asset asset) {
        QuotaHistory historyData = investmentRepository.findAllByAssetOrderByCreatedAtDesc(asset)
                .flatMap(investments -> calculateQuota(investments, asset))
                .orElseThrow(() -> new RuntimeException("Error"));

        return historyData.getTotal();
    }

    private Optional<QuotaHistory> calculateQuota(List<Investment> investments, Asset asset) {
        QuotaHistory quota = new QuotaHistory();
        quota.setDataList(new LinkedList<>());
        quota.setTotal(BigDecimal.ZERO);

        QuotaHistoryData pastValue = null;
        for (Investment investment : investments) {
            final var historyData = pastValue != null? pastValue.toBuilder().build() : null;
            pastValue = calculateQuota(investment.getValue(), historyData, investment.getDate());
            quota.setTotal(
                    quota.getTotal().add(
                            investment.getInvestmentTotal().divide(pastValue.getQuota(), 6, RoundingMode.CEILING)));
            quota.getDataList().add(pastValue);
        }
        AssetHistory history = assetHistoryRepository.findFirstByAssetOrderByDateDesc(asset)
                .orElseThrow(() -> new AssetHistoryNotFoundException(asset));
        pastValue = calculateQuota(history.getValue(), pastValue, history.getDate());
        quota.getDataList().add(pastValue);

        return Optional.of(quota);
    }

    private QuotaHistoryData calculateQuota(BigDecimal actualValue, QuotaHistoryData pastValue, LocalDate date) {
        if (isNull(pastValue))
            return new QuotaHistoryData(date, BigDecimal.ONE, actualValue);

        BigDecimal dayVariation = calculatePercentage(pastValue.getValue(), actualValue);
        BigDecimal newQuota = pastValue.getQuota().add(pastValue.getQuota().multiply(dayVariation));

        return new QuotaHistoryData(date, newQuota, actualValue);
    }

    private BigDecimal calculatePercentage(BigDecimal firstValue, BigDecimal actualValue) {
        return actualValue.subtract(firstValue).divide(firstValue, 6, RoundingMode.CEILING);
    }

}
