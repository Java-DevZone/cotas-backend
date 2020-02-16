package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
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

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void given_new_quota_without_history_should_return_zero_profitable() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(valueOf(1.189716))
                .totalQuotas(valueOf(1692)).wallet(myWallet).build();

        BigDecimal result = investmentService.calculateInvestmentsProfitability(walletHistoryMock, LocalDate.now());
        assertEquals(0, BigDecimal.valueOf(-1).compareTo(result));

    }

}