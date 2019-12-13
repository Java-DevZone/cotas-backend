package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Carteira;
import com.javadevzone.cotas.entity.Fechamento;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.FechamentoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class CarteiraService {

    private FechamentoRepository fechamentoRepository;

    private final static MathContext MATH_CONTEXT = new MathContext(6, RoundingMode.HALF_UP);

    public Carteira consolidar(Carteira carteira) {
        BigDecimal resultadoFinanceiroHoje = carteira.getAtivos()
                .stream()
                .map(ativo -> {
                    Fechamento fechamentoHoje = fechamentoRepository.findByTicket(ativo, LocalDate.now());
                    return fechamentoHoje.getValor().multiply(new BigDecimal(ativo.getQuantidade()), MATH_CONTEXT);
                }).reduce(BigDecimal::add)
                .orElseGet(() -> BigDecimal.ZERO);

        BigDecimal novaCota = calculaCota(carteira, resultadoFinanceiroHoje);

        carteira.setCota(novaCota);
        carteira.setValorTotal(resultadoFinanceiroHoje);
        carteira.setDataAtualizacaoCota(LocalDate.now());
        log.info("{}", carteira);

        return carteira;
    }

    private BigDecimal calculaCota(final Carteira carteira, final BigDecimal financeiroHoje) {
        BigDecimal financeiroOntem = carteira.getValorTotal();

        try {
            BigDecimal variacaoCota = financeiroHoje.subtract(financeiroOntem)
                    .divide(financeiroOntem, 6, RoundingMode.HALF_UP);
            BigDecimal resultado = carteira.getCota().add(carteira.getCota().multiply(variacaoCota)).setScale(6, RoundingMode.HALF_UP);
            log.info("Novo valor da cota é {}", resultado);

            return resultado;
        } catch(ArithmeticException e) {
            throw new ValoresDeFechamentoInvalidoException(e, financeiroHoje, financeiroOntem);
        }
    }
}
