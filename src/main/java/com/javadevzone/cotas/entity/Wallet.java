package com.javadevzone.cotas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal totalValue;
    private BigDecimal quota;
    private LocalDateTime quotaUpdatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany
    private Set<Asset> assets;

    @OneToMany
    private Set<QuotaHolder> quotaHolders;

}
