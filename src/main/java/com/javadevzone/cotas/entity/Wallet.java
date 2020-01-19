package com.javadevzone.cotas.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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

    @JsonIgnore
    @OneToMany(mappedBy = "wallet")
    private Set<Investment> investments;

    @JsonIgnore
    @OneToMany(mappedBy = "wallet")
    private Set<QuotaHolder> quotaHolders;

    public Wallet(Long id) {
        this.id = id;
    }

}
