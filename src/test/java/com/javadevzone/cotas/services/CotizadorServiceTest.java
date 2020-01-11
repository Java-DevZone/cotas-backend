package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.enums.AssetType;
import com.javadevzone.cotas.exceptions.ValoresDeFechamentoInvalidoException;
import com.javadevzone.cotas.repository.FechamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CotizadorServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private FechamentoRepository fechamentoRepository;

    @Test
    public void given_a_wallet_must_consolidate_a_positive_result_and_return() {
        Asset asset = Asset.builder().ticket("MGLU3").quantity(100).type(AssetType.ACAO).build();
        Wallet wallet = Wallet.builder().assets(Collections.singleton(asset)).totalValue(new BigDecimal(4250)).quota(BigDecimal.ONE).build();

        final BigDecimal valueDeOntem = new BigDecimal("42.50");
        final BigDecimal valueDeHoje = new BigDecimal("45.50");
        final BigDecimal variacaoDoDia = valueDeHoje.subtract(valueDeOntem).divide(valueDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal quotaCalculada = wallet.getQuota().add(wallet.getQuota().multiply(variacaoDoDia));

        when(fechamentoRepository.findByAssetAndDateTime(eq(asset), any(LocalDateTime.class)))
                .thenReturn(AssetHistory.builder().asset(asset).value(valueDeHoje).build());

        Wallet consolidada = walletService.consolidar(wallet);

        assertThat(consolidada.getQuota()).isEqualTo(quotaCalculada);
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(asset), any(LocalDateTime.class));
    }

    @Test
    public void given_a_wallet_must_consolidate_a_negative_result_and_return() {
        Asset ticket = Asset.builder().ticket("MGLU3").quantity(100).type(AssetType.ACAO).build();
        Wallet wallet = Wallet.builder().assets(Collections.singleton(ticket)).totalValue(BigDecimal.valueOf(4550)).quota(BigDecimal.ONE).build();

        final BigDecimal valueDeOntem = new BigDecimal("45.50");
        final BigDecimal valueDeHoje = new BigDecimal("42.50");
        final BigDecimal variacaoDoDia = valueDeHoje.subtract(valueDeOntem).divide(valueDeOntem, 6, RoundingMode.HALF_UP);
        final BigDecimal quotaCalculada = wallet.getQuota().add(wallet.getQuota().multiply(variacaoDoDia));

        when(fechamentoRepository.findByAssetAndDateTime(eq(ticket), any(LocalDateTime.class)))
                .thenReturn(AssetHistory.builder().asset(ticket).value(valueDeHoje).build());

        Wallet consolidada = walletService.consolidar(wallet);

        assertThat(consolidada.getQuota()).isEqualTo(quotaCalculada);
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(ticket), any(LocalDateTime.class));
    }

    @Test
    public void given_a_wallet_must_throw_exception_when_divided_by_zero() {
        Asset ticket = Asset.builder().ticket("MGLU3").quantity(100).type(AssetType.ACAO).build();
        Wallet wallet = Wallet.builder().assets(Collections.singleton(ticket)).totalValue(BigDecimal.valueOf(0)).quota(BigDecimal.ONE).build();

        final BigDecimal valueDeHoje = new BigDecimal("42.50");

        when(fechamentoRepository.findByAssetAndDateTime(eq(ticket), any(LocalDateTime.class)))
                .thenReturn(AssetHistory.builder().asset(ticket).value(valueDeHoje).build());

        assertThatThrownBy(() -> walletService.consolidar(wallet))
            .isInstanceOf(ValoresDeFechamentoInvalidoException.class)
            .hasMessage("Não foi possível calcular a variação para os fechamentos "
                    + valueDeHoje.multiply(new BigDecimal(ticket.getQuantity())) + " e " + wallet.getTotalValue());

        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(ticket), any(LocalDateTime.class));
    }

    @Test
    public void given_a_wallet_must_consolidate_more_than_one_stock_with_positive_result_and_return() {
        Asset mglu3 = Asset.builder().ticket("MGLU3").quantity(300).type(AssetType.ACAO).build();
        Asset vvar3 = Asset.builder().ticket("VVAR3").quantity(500).type(AssetType.ACAO).build();
        Asset petr4 = Asset.builder().ticket("PETR4").quantity(850).type(AssetType.ACAO).build();

        Wallet wallet = Wallet.builder().assets(Sets.newSet(mglu3, vvar3, petr4)).totalValue(new BigDecimal("43141.00")).quota(BigDecimal.ONE).build();

        AssetHistory petrAssetHistoryHoje = AssetHistory.builder().asset(petr4).value(new BigDecimal("29.66")).build();
        AssetHistory vvarAssetHistoryHoje = AssetHistory.builder().asset(vvar3).value(new BigDecimal("10.25")).build();
        AssetHistory mgluAssetHistoryHoje = AssetHistory.builder().asset(mglu3).value(new BigDecimal("47.25")).build();

        // mock
        when(fechamentoRepository.findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class)))
                .thenReturn(mgluAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class)))
                .thenReturn(vvarAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class)))
                .thenReturn(petrAssetHistoryHoje);

        // result
        Wallet consolidada = walletService.consolidar(wallet);

        assertThat(consolidada.getQuota())
                .isEqualTo(new BigDecimal("1.031756").setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class));
    }

    @Test
    public void given_a_wallet_must_consolidate_more_than_one_stock_with_negative_result_and_return() {
        Asset mglu3 = Asset.builder().ticket("MGLU3").quantity(300).type(AssetType.ACAO).build();
        Asset vvar3 = Asset.builder().ticket("VVAR3").quantity(500).type(AssetType.ACAO).build();
        Asset petr4 = Asset.builder().ticket("PETR4").quantity(850).type(AssetType.ACAO).build();

        Wallet wallet = Wallet.builder()
                .assets(Sets.newSet(mglu3, vvar3, petr4))
                .totalValue(new BigDecimal("43141.00")).quota(BigDecimal.ONE).build();

        AssetHistory mgluAssetHistoryHoje = AssetHistory.builder().asset(mglu3).value(new BigDecimal("47.25")).build();
        AssetHistory vvarAssetHistoryHoje = AssetHistory.builder().asset(vvar3).value(new BigDecimal("8.25")).build();
        AssetHistory petrAssetHistoryHoje = AssetHistory.builder().asset(petr4).value(new BigDecimal("22.66")).build();

        // mock
        when(fechamentoRepository.findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class)))
                .thenReturn(mgluAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class)))
                .thenReturn(vvarAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class)))
                .thenReturn(petrAssetHistoryHoje);

        // result
        Wallet consolidada = walletService.consolidar(wallet);

        assertThat(consolidada.getQuota())
                .isEqualTo(new BigDecimal("0.870657").setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class));
    }


    @Test
    public void given_a_wallet_must_consolidate_more_than_one_stock_with_result_zero_and_return() {
        Asset mglu3 = Asset.builder().ticket("MGLU3").quantity(300).type(AssetType.ACAO).build(); // 12750
        Asset vvar3 = Asset.builder().ticket("VVAR3").quantity(500).type(AssetType.ACAO).build(); // 4625
        Asset petr4 = Asset.builder().ticket("PETR4").quantity(850).type(AssetType.ACAO).build(); // 25211

        Wallet wallet = Wallet.builder()
                .assets(Sets.newSet(mglu3, vvar3, petr4))
                .totalValue(new BigDecimal("42586.0")).quota(BigDecimal.ONE).build();

        AssetHistory mgluAssetHistoryHoje = AssetHistory.builder().asset(mglu3).value(new BigDecimal("42.50")).build();
        AssetHistory vvarAssetHistoryHoje = AssetHistory.builder().asset(vvar3).value(new BigDecimal("9.25")).build();
        AssetHistory petrAssetHistoryHoje = AssetHistory.builder().asset(petr4).value(new BigDecimal("29.66")).build();

        // mock
        when(fechamentoRepository.findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class)))
                .thenReturn(mgluAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class)))
                .thenReturn(vvarAssetHistoryHoje);
        when(fechamentoRepository.findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class)))
                .thenReturn(petrAssetHistoryHoje);

        // result
        Wallet consolidada = walletService.consolidar(wallet);

        assertThat(consolidada.getQuota())
                .isEqualTo(new BigDecimal("1.0").setScale(6, RoundingMode.HALF_UP));

        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(mglu3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(vvar3), any(LocalDateTime.class));
        verify(fechamentoRepository, times(1)).findByAssetAndDateTime(eq(petr4), any(LocalDateTime.class));
    }

    @Test
    public void given_a_wallet_without_stocks_must_consolidate_the_result_and_return() {
        Wallet wallet = Wallet.builder().assets(Collections.emptySet()).totalValue(BigDecimal.ZERO).quota(BigDecimal.ONE).build();

        assertThatThrownBy(() -> walletService.consolidar(wallet))
                .isInstanceOf(ValoresDeFechamentoInvalidoException.class)
                .hasMessage("Não foi possível calcular a variação para os fechamentos 0 e 0");

        verify(fechamentoRepository, times(0)).findByAssetAndDateTime(any(), any());
    }

}
