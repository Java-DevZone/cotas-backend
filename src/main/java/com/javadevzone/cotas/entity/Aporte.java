package com.javadevzone.cotas.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Aporte {

    @Id
    private Long id;
    private BigDecimal valor;
    private LocalDate data;

    @ManyToOne
    private Cotista cotista;

}
