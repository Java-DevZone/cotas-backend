package com.javadevzone.cotas.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Aporte {

    private BigDecimal valor;
    private LocalDate data;

}
