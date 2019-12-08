package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Ativo;
import com.javadevzone.cotas.entity.Carteira;
import com.javadevzone.cotas.entity.Fechamento;
import com.javadevzone.cotas.entity.enums.TipoAtivo;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.FechamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CotizadorServiceTest {

    @InjectMocks
    private CarteiraService carteiraService;

    @Mock
    private FechamentoRepository fechamentoRepository;

    @Test
    public void given_a_carteira_must_consolidate_a_positive_result_and_return() {
        Ativo ticket = Ativo.builder().codigo("MGLU3").tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).cota(BigDecimal.ONE).build();
        final BigDecimal valorDeOntem = new BigDecimal(42.50);
        final BigDecimal valorDeHoje = new BigDecimal(45.50);
        final BigDecimal variacaoDoDia = valorDeHoje.subtract(valorDeOntem).divide(valorDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal cotaCalculada = carteira.getCota().add(carteira.getCota().multiply(variacaoDoDia));

        LocalDate ontem = LocalDate.now().minus(Period.ofDays(-1));
        when(fechamentoRepository.findByTicket(ticket, ontem))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeOntem).build());
        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota()).isEqualTo(cotaCalculada);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, ontem);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_consolidate_a_negative_result_and_return() {
        Ativo ticket = Ativo.builder().codigo("MGLU3").tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).cota(BigDecimal.ONE).build();
        final BigDecimal valorDeOntem = new BigDecimal(45.50);
        final BigDecimal valorDeHoje = new BigDecimal(42.50);
        final BigDecimal variacaoDoDia = valorDeHoje.subtract(valorDeOntem).divide(valorDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal cotaCalculada = carteira.getCota().add(carteira.getCota().multiply(variacaoDoDia));

        LocalDate ontem = LocalDate.now().minus(Period.ofDays(-1));
        when(fechamentoRepository.findByTicket(ticket, ontem))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeOntem).build());
        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota()).isEqualTo(cotaCalculada);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, ontem);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_throw_exception_when_divided_by_zero() {
        Ativo ticket = Ativo.builder().codigo("MGLU3").tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).cota(BigDecimal.ONE).build();
        final BigDecimal valorDeOntem = BigDecimal.ZERO;
        final BigDecimal valorDeHoje = new BigDecimal(42.50);

        LocalDate ontem = LocalDate.now().minus(Period.ofDays(-1));
        when(fechamentoRepository.findByTicket(ticket, ontem))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeOntem).build());
        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        assertThatThrownBy(() -> carteiraService.consolidar(carteira))
            .isInstanceOf(ValoresDeFechamentoInvalidoException.class)
            .hasMessage("Não foi possível calcular a variação para os fechamentos " + valorDeHoje + " e " + valorDeOntem);

        verify(fechamentoRepository, times(1)).findByTicket(ticket, ontem);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

}
