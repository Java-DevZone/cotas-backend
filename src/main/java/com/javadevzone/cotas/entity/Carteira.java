package com.javadevzone.cotas.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Carteira {

    @Id
    private Long id;
    private String nome;
    @OneToMany
    private List<Ativo> ativos;
    private LocalDate dataCriacao;
    @OneToMany
    private List<Cotista> cotistas;
    private BigDecimal cota;
    private BigDecimal valorTotal;
    private LocalDate dataAtualizacaoCota;

}
