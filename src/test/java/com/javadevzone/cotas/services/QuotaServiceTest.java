package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import com.javadevzone.cotas.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuotaServiceTest {

    @InjectMocks
    private QuotaService quotaService;
    @Mock
    private WalletHistoryRepository walletHistoryRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private InvestmentService investmentService;

    @Test
    public void given_a_wallet_with_a_new_investment_should_calculate_wallet_result_as_quota_and_return() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.189716")).totalQuotas(new BigDecimal("1692")).build();

        lenient().when(walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(any(Wallet.class), any(LocalDate.class)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(walletRepository.findById(anyLong())).thenReturn(ofNullable(myWallet));
        lenient().when(investmentService.calculateInvestmentsProfitability(walletHistoryMock, LocalDate.now())).thenReturn(BigDecimal.valueOf(0.016393828));
        WalletHistory walletHistory = quotaService.calculateQuotaValue(myWallet.getId(), LocalDate.now());

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.209220"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("1692"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("2046.000240"));
    }

    @Test
    public void given_a_wallet_with_more_than_one_asset_should_calculate_wallet_result_as_quota_and_return() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.086883")).totalQuotas(new BigDecimal("11371.964351")).build();

        lenient().when(walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(any(Wallet.class), any(LocalDate.class)))
                .thenReturn(ofNullable(walletHistoryMock));
        lenient().when(investmentService.calculateInvestmentsProfitability(any(WalletHistory.class), any(LocalDate.class))).thenReturn(BigDecimal.valueOf(-0.009303669));
        WalletHistory walletHistory = quotaService.calculateQuotaValue(myWallet.getId(), LocalDate.now().minus(1, ChronoUnit.DAYS));

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.076772"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("11371.964351"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("12245.012799"));
    }

    @Test
    public void given_a_wallet_with_2_assets_and_a_negative_investment_should_calculate_wallet_result_as_quota_and_return() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder()
                .quota(new BigDecimal("1.241210"))
                .totalQuotas(new BigDecimal("11371.964351")).build();
        lenient().when(walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(any(Wallet.class), any(LocalDate.class)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(walletRepository.findById(anyLong())).thenReturn(ofNullable(myWallet));
        lenient().when(investmentService.calculateInvestmentsProfitability(any(WalletHistory.class), any(LocalDate.class))).thenReturn(BigDecimal.valueOf(-0.006021543));
        WalletHistory walletHistory = quotaService.calculateQuotaValue(myWallet.getId(), LocalDate.now().minus(1, ChronoUnit.DAYS));

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.233737"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("11371.964351"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("14030.013183"));
    }

    @Test
    public void given_a_wallet_should_calculate_wallet_result_as_quota_and_return() {
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.209220")).totalQuotas(new BigDecimal("2819.999868")).build();

        lenient().when(walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(any(Wallet.class), any(LocalDate.class)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(walletRepository.findById(anyLong())).thenReturn(ofNullable(myWallet));
        lenient().when(investmentService.calculateInvestmentsProfitability(any(WalletHistory.class), any(LocalDate.class))).thenReturn(BigDecimal.valueOf(-0.01319611));
        WalletHistory walletHistory = quotaService.calculateQuotaValue(myWallet.getId(), LocalDate.now().minus(1, ChronoUnit.DAYS));

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.193263"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("2819.999868"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("3365.001503"));
    }

    @Test
    public void given_a_wallet_should_calculate_wallet_result_when_not_has_not_previous_quota() {
        Wallet myWallet = Wallet.builder().id(1L).build();

        lenient().when(walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(any(Wallet.class), any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(walletRepository.findById(anyLong())).thenReturn(ofNullable(myWallet));
        lenient().when(investmentService.calculateInvestmentsProfitability(any(WalletHistory.class), any(LocalDate.class))).thenReturn(BigDecimal.valueOf(0));
        WalletHistory walletHistory = quotaService.calculateQuotaValue(myWallet.getId(), LocalDate.now().minus(1, ChronoUnit.DAYS));

        assertThat(walletHistory.getQuota().compareTo(BigDecimal.ONE)).isEqualTo(0);
    }

}
