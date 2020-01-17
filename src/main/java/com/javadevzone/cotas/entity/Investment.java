package com.javadevzone.cotas.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal value;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne
    private Asset asset;

    @ManyToOne
    private QuotaHolder quotaHolder;

    @JsonManagedReference
    @ManyToOne
    private Wallet wallet;

}
