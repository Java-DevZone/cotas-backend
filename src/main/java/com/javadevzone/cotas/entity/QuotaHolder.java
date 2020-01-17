package com.javadevzone.cotas.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
public class QuotaHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate optInAt;
    private LocalDate optOutAt;

    @ManyToOne
    private Wallet wallet;

}
