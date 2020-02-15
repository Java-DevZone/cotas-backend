package com.javadevzone.cotas.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import com.javadevzone.cotas.exceptions.AssetNotFoundException;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import com.javadevzone.cotas.services.QuotaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/quota")
public class QuotaController {

    private final QuotaService quotaService;
    private final WalletHistoryRepository walletHistoryRepository;

    @ResponseStatus(OK)
    @PostMapping("/{walletId}/recalculate")
    public WalletHistory recalculateQuotaForDate(@PathVariable Long walletId,
                                                 @RequestBody  QuotaForDate quotaForDate) {
        try {
            log.info("Data recebida: {}", quotaForDate.getDate());
            WalletHistory walletHistory = quotaService.calculateQuotaValue(walletId, quotaForDate.getDate());
            return walletHistoryRepository.save(walletHistory);
        } catch (AssetNotFoundException assetNotFound) {
            throw new ResponseStatusException(NOT_FOUND, assetNotFound.getMessage(), assetNotFound);
        }
    }

    @Getter @Setter
    static class QuotaForDate {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        private LocalDate date;

    }

}
