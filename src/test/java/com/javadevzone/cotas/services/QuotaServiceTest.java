package com.javadevzone.cotas.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javadevzone.cotas.entity.*;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.AssetRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuotaServiceTest {

    @InjectMocks
    private QuotaService quotaService;

    @Mock
    private AssetHistoryRepository assetHistoryRepository;
    @Mock
    private InvestmentRepository investmentRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private WalletHistoryRepository walletHistoryRepository;

    private final static String RESOURCES_PATH = "src/test/resources/";

    @Test
    public void given_a_wallet_with_a_new_investment_should_calculate_wallet_result_as_quota_and_return() {
        Asset jhsf3 = Asset.builder().ticket("JHSF3").build();
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.189716")).totalQuotas(new BigDecimal("1692")).build();

        List<Investment> investments = singletonList(
                Investment.builder().quantity(200L).value(new BigDecimal("6.82")).asset(jhsf3).build());

        when(investmentRepository.findAllByWalletAndDate(myWallet, LocalDate.now()))
                .thenReturn(investments);
        when(walletHistoryRepository.findByWalletAndRegisterDate(myWallet, LocalDate.now().minus(1, ChronoUnit.DAYS)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(assetRepository.findAssetsByWallet(myWallet))
                .thenReturn(of(singletonList(jhsf3)));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(jhsf3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("6.82")).asset(jhsf3).quantity(new BigDecimal("500")).build()));

        WalletHistory walletHistory = quotaService.calculateWalletQuota(myWallet);

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.209220"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("2819.999868"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("3410.000241"));
    }

    @Test
    public void given_a_wallet_with_more_than_one_asset_should_calculate_wallet_result_as_quota_and_return() {
        Asset jhsf3 = Asset.builder().ticket("JHSF3").build();
        Asset movi3 = Asset.builder().ticket("MOVI3").build();
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.086883")).totalQuotas(new BigDecimal("11371.964351")).build();

        when(investmentRepository.findAllByWalletAndDate(myWallet, LocalDate.now()))
                .thenReturn(Collections.emptyList());
        when(walletHistoryRepository.findByWalletAndRegisterDate(myWallet, LocalDate.now().minus(1, ChronoUnit.DAYS)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(assetRepository.findAssetsByWallet(myWallet))
                .thenReturn(of(asList(jhsf3, movi3)));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(jhsf3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("6.73")).asset(jhsf3).quantity(new BigDecimal("500")).build()));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(movi3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("17.76")).asset(movi3).quantity(new BigDecimal("500")).build()));

        WalletHistory walletHistory = quotaService.calculateWalletQuota(myWallet);

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.076771"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("11371.964351"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("12245.001427"));
    }

    @Test
    public void given_a_wallet_with_2_assets_and_a_negative_investment_should_calculate_wallet_result_as_quota_and_return() {
        Asset jhsf3 = Asset.builder().ticket("JHSF3").build();
        Asset movi3 = Asset.builder().ticket("MOVI3").build();
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder()
                .quota(new BigDecimal("1.241210"))
                .totalQuotas(new BigDecimal("11371.964351")).build();
        List<Investment> investments = singletonList(Investment.builder()
                .quantity(-100L)
                .value(new BigDecimal("8.06"))
                .asset(jhsf3).build());

        when(investmentRepository.findAllByWalletAndDate(myWallet, LocalDate.now()))
                .thenReturn(investments);
        when(walletHistoryRepository.findByWalletAndRegisterDate(myWallet, LocalDate.now().minus(1, ChronoUnit.DAYS)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(assetRepository.findAssetsByWallet(myWallet))
                .thenReturn(of(asList(jhsf3, movi3)));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(jhsf3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("8.06")).asset(jhsf3).quantity(new BigDecimal("400")).build()));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(movi3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("20.00")).asset(movi3).quantity(new BigDecimal("500")).build()));

        WalletHistory walletHistory = quotaService.calculateWalletQuota(myWallet);

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.233736"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("10718.664132"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("13224.001812"));
    }

    @Test
    public void given_a_wallet_should_calculate_wallet_result_as_quota_and_return() {
        Asset jhsf3 = Asset.builder().ticket("JHSF3").build();
        Wallet myWallet = Wallet.builder().id(1L).build();
        WalletHistory walletHistoryMock = WalletHistory.builder().quota(new BigDecimal("1.209220")).totalQuotas(new BigDecimal("2819.999868")).build();

        when(investmentRepository.findAllByWalletAndDate(myWallet, LocalDate.now()))
                .thenReturn(Collections.emptyList());
        when(walletHistoryRepository.findByWalletAndRegisterDate(myWallet, LocalDate.now().minus(1, ChronoUnit.DAYS)))
                .thenReturn(ofNullable(walletHistoryMock));
        when(assetRepository.findAssetsByWallet(myWallet))
                .thenReturn(of(singletonList(jhsf3)));
        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(jhsf3))
                .thenReturn(of(AssetHistory.builder().value(new BigDecimal("6.73")).asset(jhsf3).quantity(new BigDecimal("500")).build()));

        WalletHistory walletHistory = quotaService.calculateWalletQuota(myWallet);

        assertThat(walletHistory.getQuota())
                .isEqualTo(new BigDecimal("1.193263"));
        assertThat(walletHistory.getTotalQuotas())
                .isEqualTo(new BigDecimal("2819.999868"));
        assertThat(walletHistory.getWalletValue())
                .isEqualTo(new BigDecimal("3365.001503"));
    }

    @Test
    public void given_a_wallet_should_calculate_wallet_result_when_not_has_not_previous_quota() {
        Asset jhsf3 = Asset.builder().ticket("JHSF3").build();
        Wallet myWallet = Wallet.builder().id(1L).build();

        List<Investment> investments = singletonList(
                Investment.builder().quantity(100L).value(new BigDecimal("8.25")).asset(jhsf3).build());

        when(investmentRepository.findAllByWalletAndDate(myWallet, LocalDate.now()))
                .thenReturn(investments);

        when(walletHistoryRepository.findByWalletAndRegisterDate(myWallet, LocalDate.now().minus(1, ChronoUnit.DAYS)))
                .thenReturn(Optional.empty());
        when(assetRepository.findAssetsByWallet(myWallet))
                .thenReturn(Optional.empty());

        WalletHistory walletHistory = quotaService.calculateWalletQuota(myWallet);

        assertThat(walletHistory.getQuota())
                .isEqualTo(BigDecimal.ONE);
    }

    private List<Investment> loadInvestments() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(Files.readString(Paths.get(RESOURCES_PATH,"mock-investments.json")), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
