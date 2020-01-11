package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.AssetHistory;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.FechamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private FechamentoRepository fechamentoRepository;

    private final static MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

    public Wallet consolidar(Wallet wallet) {
        BigDecimal resultadoFinanceiroHoje = wallet.getAssets()
                .stream()
                .map(ativo -> {
                    AssetHistory fechamentoHoje = fechamentoRepository.findByAssetAndDateTime(ativo, LocalDateTime.now());
                    return fechamentoHoje.getValue().multiply(new BigDecimal(ativo.getQuantity()), MATH_CONTEXT);
                }).reduce(BigDecimal::add)
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
}
