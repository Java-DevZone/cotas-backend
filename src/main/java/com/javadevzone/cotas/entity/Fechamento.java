package com.javadevzone.cotas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fechamento {

    private Long id;
    private Ativo ticket;
    private BigDecimal valor;
    private LocalDate data;

}

