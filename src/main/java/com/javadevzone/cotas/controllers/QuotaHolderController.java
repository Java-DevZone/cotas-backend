package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.QuotaHolder;
import com.javadevzone.cotas.exceptions.RequiredAttributeException;
import com.javadevzone.cotas.repository.QuotaHolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public QuotaHolder atualizar(@PathVariable @NotNull Long id, @RequestBody QuotaHolder quotaHolder) {
        if (Objects.isNull(quotaHolder.getName()))
            throw new RequiredAttributeException("O nome do Investidor é obrigatório");

        quotaHolder.setId(id);
        return quotaHolderRepository.save(quotaHolder);
    }

}
