package com.javadevzone.cotas.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javadevzone.cotas.dto.QuotaHistory;
import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.entity.AssetHistory;
import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.enums.AssetType;
import com.javadevzone.cotas.exceptions.AssetNotFoundException;
import com.javadevzone.cotas.repository.AssetHistoryRepository;
import com.javadevzone.cotas.repository.InvestmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuotaServiceTest {

    @InjectMocks
    private QuotaService quotaService;

    @Mock
    private AssetHistoryRepository assetHistoryRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    private final static String RESOURCES_PATH = "src/test/resources/";

    @Test
    public void given_an_asset_should_calculate_result_as_a_quota_and_return() {
        Asset asset = Asset.builder().ticket("JHSF3").build();
        LocalDate jan17 = LocalDate.of(2020, 1, 17);

        when(investmentRepository.findAllByAssetOrderByDateAsc(asset))
                .thenReturn(loadJhsfInvestments());

        when(assetHistoryRepository.findFirstByAssetOrderByDateDesc(asset))
                .thenReturn(of(new AssetHistory(32L, new BigDecimal("8.64"), jan17, asset)));

        QuotaHistory quota = quotaService.calculateQuotaFor(asset);

        assertThat(quota.getQuotaTotal().setScale(6, RoundingMode.CEILING))
                .isEqualTo(new BigDecimal("1691.999797"));
        assertThat(quota.getLastHistoryData().getQuota().setScale(6, RoundingMode.CEILING))
                .isEqualTo(new BigDecimal("1.531918"));
    }

    @Test
    public void given_an_asset_that_doesnt_exists_should_throws_not_found_exception() {
        Asset asset = Asset.builder().ticket("JHSF3").build();

        when(investmentRepository.findAllByAssetOrderByDateAsc(asset))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> quotaService.calculateQuotaFor(asset))
                .isInstanceOf(AssetNotFoundException.class)
                .hasMessage("Não foi possível encontra a Asset com Ticket JHSF3");
    }

    private Optional<List<Investment>> loadJhsfInvestments() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return of(objectMapper.readValue(Files.readString(Paths.get(RESOURCES_PATH,"jhsf-investments.json")), new TypeReference<>() {}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Optional<List<T>> loadJsonFileToObject(String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return of(objectMapper.readValue(Files.readString(Paths.get(RESOURCES_PATH,fileName)), new TypeReference<>() {}));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
