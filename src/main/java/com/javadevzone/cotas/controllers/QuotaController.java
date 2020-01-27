package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import com.javadevzone.cotas.exceptions.AssetNotFoundException;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import com.javadevzone.cotas.services.QuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quota")
public class QuotaController {

    private final QuotaService quotaService;
    private final WalletHistoryRepository walletHistoryRepository;

    @ResponseStatus(OK)
    @PostMapping("/{walletId}")
    public WalletHistory calculateQuota(@PathVariable Long walletId) {
        try {
            WalletHistory walletHistory = quotaService.calculateWalletQuota(new Wallet(walletId));
            return walletHistoryRepository.save(walletHistory);
        } catch (AssetNotFoundException assetNotFound) {
            throw new ResponseStatusException(NOT_FOUND, assetNotFound.getMessage(), assetNotFound);
        }
    }

}
