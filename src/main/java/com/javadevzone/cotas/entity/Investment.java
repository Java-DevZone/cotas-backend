package com.javadevzone.cotas.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal value;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne
    @NotNull
    private Asset asset;

    @ManyToOne
    private QuotaHolder quotaHolder;

    @JsonManagedReference
    @ManyToOne
    private Wallet wallet;

}
