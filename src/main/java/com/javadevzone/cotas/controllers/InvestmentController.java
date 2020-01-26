package com.javadevzone.cotas.controllers;

import com.javadevzone.cotas.entity.Investment;
import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentRepository investmentRepository;

    @PostMapping
    @ResponseStatus(CREATED)
    public Investment create(@RequestBody final Investment investment) {
        investment.setCreatedAt(LocalDateTime.now());

        return investmentRepository.save(investment);
    }

    @PutMapping
    @ResponseStatus(OK)
    public Investment update(@RequestBody final Investment investment) {
        investment.setUpdatedAt(LocalDateTime.now());

        return investmentRepository.save(investment);
    }

    @GetMapping("/{investment.id}")
    public ResponseEntity<Investment> get(@PathVariable("investment.id") Long investmentId) {
        return investmentRepository
                .findById(investmentId)
                .map(investment -> ResponseEntity.status(HttpStatus.OK).body(investment))
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, format("Investment com ID [%s] não foi encontrado.", investmentId)));
    }

    @GetMapping
    public ResponseEntity<List<Investment>> getAllByWallet(Long walletId) {
        log.info("Wallet ID {}", walletId);
        return Optional.ofNullable(investmentRepository
                .findAllByWalletOrderByDateAsc(Wallet.builder().id(walletId).build()))
                .map(investments -> ResponseEntity.status(HttpStatus.OK).body(investments))
                .orElseThrow(() ->
                        new ResponseStatusException(NOT_FOUND, format("Investments com Wallet ID [%s] não foram encontrados.", walletId)));
    }

}
