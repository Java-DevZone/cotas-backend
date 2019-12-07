package com.javadevzone.cotas.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class Carteira {

    private String nome;
    private List<Ativo> ativos;
    private LocalDate dataCriacao;
    private List<Cotista> cotistas;
    private BigDecimal cota;

}
