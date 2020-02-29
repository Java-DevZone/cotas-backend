package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.QuotaHolder;
import com.javadevzone.cotas.exceptions.RequiredAttributeException;
import com.javadevzone.cotas.repository.QuotaHolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/quotaHolder")
@AllArgsConstructor
public class QuotaHolderController {

    private final QuotaHolderRepository quotaHolderRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public QuotaHolder create(@RequestBody QuotaHolder quotaHolder) {
        if (Objects.isNull(quotaHolder.getName()))
            throw new RequiredAttributeException("O nome do Investidor é obrigatório");

        return quotaHolderRepository.save(quotaHolder);
    }

}
