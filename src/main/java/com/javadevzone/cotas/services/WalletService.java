package com.javadevzone.cotas.services;

import com.javadevzone.cotas.dto.QuotaHistory;
import com.javadevzone.cotas.dto.QuotaHistory.QuotaHistoryData;
import com.javadevzone.cotas.entity.AssetHistory;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final AssetHistoryRepository assetHistoryRepository;
    private final InvestmentRepository investmentRepository;

    private final static MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

    public Wallet consolidar(Wallet wallet) {
        BigDecimal resultadoFinanceiroHoje = wallet.getInvestments()
                .stream()
                .map(investment -> assetHistoryRepository.findByAssetAndDate(investment.getAsset(), LocalDate.now())
                            .flatMap(assetHistory -> Optional.of(assetHistory.getValue().multiply(new BigDecimal(investment.getQuantity()), MATH_CONTEXT)))
                            .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        BigDecimal novaCota = calculaCota(wallet, resultadoFinanceiroHoje);

        wallet.setQuota(novaCota);
        wallet.setTotalValue(resultadoFinanceiroHoje);
        wallet.setQuotaUpdatedAt(LocalDateTime.now());
        log.info("{}", wallet);

        return wallet;
    }

    private BigDecimal calculaCota(final Wallet wallet, final BigDecimal financeiroHoje) {
        BigDecimal financeiroOntem = wallet.getTotalValue();

        try {
            BigDecimal variacaoCota = financeiroHoje.subtract(financeiroOntem)
                    .divide(financeiroOntem, 6, RoundingMode.HALF_UP);
            BigDecimal resultado = wallet.getQuota().add(wallet.getQuota().multiply(variacaoCota)).setScale(6, RoundingMode.HALF_UP);
            log.info("Novo valor da cota é {}", resultado);

            return resultado;
        } catch(ArithmeticException e) {
            throw new ValoresDeFechamentoInvalidoException(e, financeiroHoje, financeiroOntem);
        }
    }

    public List<QuotaHistory> calculateQuotaValueFrom(Long walletId, LocalDateTime date) {
        return investmentRepository.findAllByWallet(new Wallet(walletId))
                .stream()
                .map(investment -> {
                    List<QuotaHistoryData> quotaHistoryData = assetHistoryRepository
                            .findAllByAssetAndDateAfterOrderByDateAsc(investment.getAsset(), investment.getCreatedAt().toLocalDate())
                            .flatMap(this::calculateQuotaHistory)
                            .orElse(Collections.emptyList());
                    return new QuotaHistory(investment.getAsset().getTicket(), quotaHistoryData);
                })
                .collect(Collectors.toList());
    }

    private Optional<List<QuotaHistoryData>> calculateQuotaHistory(List<AssetHistory> assetHistories) {
        List<QuotaHistoryData> dataList = new ArrayList<>();
        QuotaHistoryData lastValue = null;

        for (AssetHistory history : assetHistories) {
            if (isNull(lastValue)) {
                lastValue = new QuotaHistoryData(history.getDate(), BigDecimal.ONE, history.getValue());
            } else {
                BigDecimal newQuotaValue = calculateQuota(lastValue, history);

                lastValue = new QuotaHistoryData(history.getDate(), newQuotaValue, history.getValue());
            }
            dataList.add(lastValue);
        }

        return Optional.of(dataList);
    }

    private BigDecimal calculateQuota(QuotaHistoryData yesterdayValue, AssetHistory history) {
        BigDecimal dayVariation = calculatePercentage(yesterdayValue.getValue(), history.getValue());
        return yesterdayValue.getQuota().add(yesterdayValue.getQuota().multiply(dayVariation));
    }

    private BigDecimal calculatePercentage(BigDecimal firstValue, BigDecimal actualValue) {
        return actualValue.subtract(firstValue).divide(firstValue, 6, RoundingMode.CEILING);
    }

}
