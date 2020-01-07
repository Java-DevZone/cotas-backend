package com.javadevzone.cotas.cotrollers;

import com.javadevzone.cotas.entity.Ativo;
import com.javadevzone.cotas.repository.AtivoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ativos")
@ResponseBody
public class AtivoController {

    private final AtivoRepository ativoRepository;

    @PostMapping
    public Ativo create(Ativo ativo) {
        Ativo savedAtivo = ativoRepository.save(ativo);
        log.info("Salvando Ativo {}", savedAtivo);

        return savedAtivo;
    }



}
