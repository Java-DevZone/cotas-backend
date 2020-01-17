package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/assets")
@ResponseBody
public class AssetController {

    private final AssetRepository assetRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Asset create(Asset asset) {
        Asset savedAsset = assetRepository.save(asset);
        log.info("Salvando Ativo {}", savedAsset);

        return savedAsset;
    }



}
