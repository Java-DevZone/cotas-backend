package com.javadevzone.cotas.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Carteira {

    private String nome;
    private List<Ativo> ativos;
    private LocalDate dataCriacao;
    private List<Cotista> cotistas;
    private BigDecimal cota;
    private BigDecimal valorTotal;
    private LocalDate dataAtualizacaoCota;

}
