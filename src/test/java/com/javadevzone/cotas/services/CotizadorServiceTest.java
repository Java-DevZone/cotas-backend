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
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
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
        Ativo ticket = Ativo.builder().codigo("MGLU3").quantidade(100).tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).valorTotal(new BigDecimal(4250)).cota(BigDecimal.ONE).build();

        final BigDecimal valorDeOntem = new BigDecimal(42.50);
        final BigDecimal valorDeHoje = new BigDecimal(45.50);
        final BigDecimal variacaoDoDia = valorDeHoje.subtract(valorDeOntem).divide(valorDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal cotaCalculada = carteira.getCota().add(carteira.getCota().multiply(variacaoDoDia));

        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota()).isEqualTo(cotaCalculada);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_consolidate_a_negative_result_and_return() {
        Ativo ticket = Ativo.builder().codigo("MGLU3").quantidade(100).tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).valorTotal(BigDecimal.valueOf(4550)).cota(BigDecimal.ONE).build();

        final BigDecimal valorDeOntem = new BigDecimal(45.50);
        final BigDecimal valorDeHoje = new BigDecimal(42.50);
        final BigDecimal variacaoDoDia = valorDeHoje.subtract(valorDeOntem).divide(valorDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal cotaCalculada = carteira.getCota().add(carteira.getCota().multiply(variacaoDoDia));

        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota()).isEqualTo(cotaCalculada);
        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_throw_exception_when_divided_by_zero() {
        Ativo ticket = Ativo.builder().codigo("MGLU3").quantidade(100).tipo(TipoAtivo.ACAO).build();
        Carteira carteira = Carteira.builder().ativos(Collections.singletonList(ticket)).valorTotal(BigDecimal.valueOf(0)).cota(BigDecimal.ONE).build();

        final BigDecimal valorDeHoje = new BigDecimal(42.50);

        when(fechamentoRepository.findByTicket(ticket, LocalDate.now()))
                .thenReturn(Fechamento.builder().ticket(ticket).valor(valorDeHoje).build());

        assertThatThrownBy(() -> carteiraService.consolidar(carteira))
            .isInstanceOf(ValoresDeFechamentoInvalidoException.class)
            .hasMessage("Não foi possível calcular a variação para os fechamentos "
                    + valorDeHoje.multiply(new BigDecimal(ticket.getQuantidade())) + " e " + carteira.getValorTotal());

        verify(fechamentoRepository, times(1)).findByTicket(ticket, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_consolidate_more_than_one_stock_with_positive_result_and_return() {
        Ativo mglu3 = Ativo.builder().codigo("MGLU3").quantidade(300).tipo(TipoAtivo.ACAO).build();
        Ativo vvar3 = Ativo.builder().codigo("VVAR3").quantidade(500).tipo(TipoAtivo.ACAO).build();
        Ativo petr4 = Ativo.builder().codigo("PETR4").quantidade(850).tipo(TipoAtivo.ACAO).build();

        Carteira carteira = Carteira.builder().ativos(Arrays.asList(mglu3, vvar3, petr4)).valorTotal(new BigDecimal(43141.00)).cota(BigDecimal.ONE).build();

        Fechamento petrFechamentoHoje = Fechamento.builder().ticket(petr4).valor(new BigDecimal(29.66)).build();
        Fechamento vvarFechamentoHoje = Fechamento.builder().ticket(vvar3).valor(new BigDecimal(10.25)).build();
        Fechamento mgluFechamentoHoje = Fechamento.builder().ticket(mglu3).valor(new BigDecimal(47.25)).build();

        // mock
        when(fechamentoRepository.findByTicket(mglu3, LocalDate.now()))
                .thenReturn(mgluFechamentoHoje);
        when(fechamentoRepository.findByTicket(vvar3, LocalDate.now()))
                .thenReturn(vvarFechamentoHoje);
        when(fechamentoRepository.findByTicket(petr4, LocalDate.now()))
                .thenReturn(petrFechamentoHoje);

        // result
        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota())
                .isEqualTo(new BigDecimal(1.031756).setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByTicket(mglu3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(vvar3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(petr4, LocalDate.now());
    }

    @Test
    public void given_a_carteira_must_consolidate_more_than_one_stock_with_negative_result_and_return() {
        Ativo mglu3 = Ativo.builder().codigo("MGLU3").quantidade(300).tipo(TipoAtivo.ACAO).build();
        Ativo vvar3 = Ativo.builder().codigo("VVAR3").quantidade(500).tipo(TipoAtivo.ACAO).build();
        Ativo petr4 = Ativo.builder().codigo("PETR4").quantidade(850).tipo(TipoAtivo.ACAO).build();

        Carteira carteira = Carteira.builder()
                .ativos(Arrays.asList(mglu3, vvar3, petr4))
                .valorTotal(new BigDecimal(43141.00)).cota(BigDecimal.ONE).build();

        Fechamento mgluFechamentoHoje = Fechamento.builder().ticket(mglu3).valor(new BigDecimal(47.25)).build();
        Fechamento vvarFechamentoHoje = Fechamento.builder().ticket(vvar3).valor(new BigDecimal(8.25)).build();
        Fechamento petrFechamentoHoje = Fechamento.builder().ticket(petr4).valor(new BigDecimal(22.66)).build();

        // mock
        when(fechamentoRepository.findByTicket(mglu3, LocalDate.now()))
                .thenReturn(mgluFechamentoHoje);
        when(fechamentoRepository.findByTicket(vvar3, LocalDate.now()))
                .thenReturn(vvarFechamentoHoje);
        when(fechamentoRepository.findByTicket(petr4, LocalDate.now()))
                .thenReturn(petrFechamentoHoje);

        // result
        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota())
                .isEqualTo(new BigDecimal("0.870657").setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByTicket(mglu3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(vvar3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(petr4, LocalDate.now());
    }


    @Test
    public void given_a_carteira_must_consolidate_more_than_one_stock_with_result_zero_and_return() {
        Ativo mglu3 = Ativo.builder().codigo("MGLU3").quantidade(300).tipo(TipoAtivo.ACAO).build(); // 12750
        Ativo vvar3 = Ativo.builder().codigo("VVAR3").quantidade(500).tipo(TipoAtivo.ACAO).build(); // 4625
        Ativo petr4 = Ativo.builder().codigo("PETR4").quantidade(850).tipo(TipoAtivo.ACAO).build(); // 25211

        Carteira carteira = Carteira.builder()
                .ativos(Arrays.asList(mglu3, vvar3, petr4))
                .valorTotal(new BigDecimal("42586.0")).cota(BigDecimal.ONE).build();

        Fechamento mgluFechamentoHoje = Fechamento.builder().ticket(mglu3).valor(new BigDecimal(42.50)).build();
        Fechamento vvarFechamentoHoje = Fechamento.builder().ticket(vvar3).valor(new BigDecimal(9.25)).build();
        Fechamento petrFechamentoHoje = Fechamento.builder().ticket(petr4).valor(new BigDecimal(29.66)).build();

        // mock
        when(fechamentoRepository.findByTicket(mglu3, LocalDate.now()))
                .thenReturn(mgluFechamentoHoje);
        when(fechamentoRepository.findByTicket(vvar3, LocalDate.now()))
                .thenReturn(vvarFechamentoHoje);
        when(fechamentoRepository.findByTicket(petr4, LocalDate.now()))
                .thenReturn(petrFechamentoHoje);

        // result
        Carteira consolidada = carteiraService.consolidar(carteira);

        assertThat(consolidada.getCota())
                .isEqualTo(new BigDecimal("1.0").setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByTicket(mglu3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(vvar3, LocalDate.now());
        verify(fechamentoRepository, times(1)).findByTicket(petr4, LocalDate.now());
    }

    @Test
    public void given_a_carteira_without_stocks_must_consolidate_the_result_and_return() {
        Carteira carteira = Carteira.builder().ativos(Collections.emptyList()).valorTotal(new BigDecimal(0)).cota(BigDecimal.ONE).build();

        assertThatThrownBy(() -> carteiraService.consolidar(carteira))
                .isInstanceOf(ValoresDeFechamentoInvalidoException.class)
                .hasMessage("Não foi possível calcular a variação para os fechamentos 0 e 0");

        verify(fechamentoRepository, times(0)).findByTicket(any(), any());
    }

}
