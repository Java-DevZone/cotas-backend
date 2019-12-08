package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Carteira;
import com.javadevzone.cotas.entity.Fechamento;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.FechamentoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@AllArgsConstructor
@Slf4j
public class CarteiraService {

    private FechamentoRepository fechamentoRepository;
//    private CarteiraRepository carteiraRepository;

    public Carteira consolidar(Carteira carteira) {
        BigDecimal cotaCalculada = carteira.getAtivos()
                .stream()
                .map(ativo -> {
                    Fechamento fechamentoHoje = fechamentoRepository.findByTicket(ativo, LocalDate.now());
                    Fechamento fechamentoOntem = fechamentoRepository.findByTicket(ativo, LocalDate.now().minus(Period.ofDays(-1)));

                    BigDecimal variacaoDoAtivo = calculaVariacaoAtivo(fechamentoHoje, fechamentoOntem);
                    BigDecimal variacaoCota = carteira.getCota().add(carteira.getCota().multiply(variacaoDoAtivo));
                    return variacaoCota;
                }).reduce(BigDecimal::add).get();

        carteira.setCota(cotaCalculada);
        log.info("{}", carteira);
//        carteiraRepository.save(carteira);

        return carteira;
    }

    private BigDecimal calculaVariacaoAtivo(Fechamento fechamentoHoje, Fechamento fechamentoOntem) {
        try {
            return fechamentoHoje.getValor().subtract(fechamentoOntem.getValor())
                    .divide(fechamentoOntem.getValor(), 6, RoundingMode.HALF_UP);
        } catch(ArithmeticException e) {
            throw new ValoresDeFechamentoInvalidoException(e, fechamentoHoje, fechamentoOntem);
        }
    }
}
