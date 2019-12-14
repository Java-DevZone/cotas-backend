package com.javadevzone.cotas.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Cotista {

    @Id
    private Long id;
    private String name;
    @OneToMany(mappedBy = "cotista")
    private List<Aporte> aportes;

    private LocalDate dataEntrada;
    private LocalDate dataSaida;

}
