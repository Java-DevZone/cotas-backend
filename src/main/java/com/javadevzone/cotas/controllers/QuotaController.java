package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.dto.QuotaHistory;
import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.exceptions.AssetNotFoundException;
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

    @ResponseStatus(OK)
    @PostMapping("/{ticket}")
    public QuotaHistory calculateQuota(@PathVariable String ticket) {
        try {
            return quotaService.calculateQuotaFor(new Asset(ticket));
        } catch (AssetNotFoundException assetNotFound) {
            throw new ResponseStatusException(NOT_FOUND, assetNotFound.getMessage(), assetNotFound);
        }
    }

}
