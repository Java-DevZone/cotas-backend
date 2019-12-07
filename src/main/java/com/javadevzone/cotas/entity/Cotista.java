package com.javadevzone.cotas.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Cotista {

    private String name;
    private List<Aporte> aportes;
    private LocalDate dataEntrada;
    private LocalDate dataSaida;

}
