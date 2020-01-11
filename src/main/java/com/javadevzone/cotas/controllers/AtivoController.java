package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.Asset;
import com.javadevzone.cotas.repository.AtivoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ativos")
@ResponseBody
public class AtivoController {

    private final AtivoRepository ativoRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Asset create(Asset asset) {
        Asset savedAsset = ativoRepository.save(asset);
        log.info("Salvando Ativo {}", savedAsset);

        return savedAsset;
    }



}
