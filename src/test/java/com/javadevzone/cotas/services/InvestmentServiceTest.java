package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.*;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.AssetRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private InvestmentRepository investmentRepository;
    @Mock
    private AssetHistoryRepository assetHistoryRepository;


    @Test
    public void given_new_quota_without_history_should_return_negative_profitable() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(valueOf(1.189716))
                .totalQuotas(valueOf(1692)).wallet(myWallet).build();

        BigDecimal result = investmentService.calculateInvestmentsProfitability(walletHistoryMock, LocalDate.now());
        assertEquals(0, BigDecimal.valueOf(-1).compareTo(result));

    }

    @Test
    public void given_history_and_assets_will_calculate_the_profitability() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(valueOf(1.189716))
                .totalQuotas(valueOf(1692)).wallet(myWallet).build();
        Asset jhsfTick = Asset.builder().ticket("JHSF3").build();
        when(investmentRepository.getQuantityByWalletAndAssetAndDateBefore(myWallet, jhsfTick, LocalDate.now()))
                .thenReturn(of(200L));
        when(assetHistoryRepository.findByAssetAndDate(any(Asset.class), any(LocalDate.class)))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("6.82")).asset(jhsfTick).build()));
        when(assetRepository.findInvestedAssetsByDate(any(Wallet.class), any(LocalDate.class))).thenReturn(of(singletonList(jhsfTick)));

        BigDecimal result = investmentService.calculateInvestmentsProfitability(walletHistoryMock, LocalDate.now());
        assertEquals(0, BigDecimal.valueOf(-0.32240419).compareTo(result));

    }

}