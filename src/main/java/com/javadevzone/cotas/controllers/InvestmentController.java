package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.repository.InvestmentRepository;
import com.javadevzone.cotas.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentRepository investmentRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public Investment create(@RequestBody final Investment investment) {
        investment.setDateTime(LocalDateTime.now());

        return investmentRepository.save(investment);
    }

    @PutMapping
    @ResponseStatus(OK)
    public Investment update(@RequestBody final Investment investment) {
        investment.setUpdatedAt(LocalDateTime.now());

        return investmentRepository.save(investment);
    }

    @GetMapping("/{wallet.id}")
    public ResponseEntity<Investment> get(@PathVariable("wallet.id") Long investmentId) {
        return investmentRepository
                .findById(investmentId)
                .map(investment -> ResponseEntity.status(HttpStatus.OK).body(investment))
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, format("Investment com ID [%s] não foi encontrado.", investmentId)));
    }

}
