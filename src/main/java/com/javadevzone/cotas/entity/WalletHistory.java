package com.javadevzone.cotas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.util.Objects.isNull;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WalletHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private BigDecimal quota;
    private BigDecimal totalQuotas;

    private LocalDate registerDate;

    @Transient
    public BigDecimal getWalletValue() {
        return this.quota.multiply(this.totalQuotas).setScale(6, RoundingMode.CEILING);
    }

    public void addTotalQuotas(BigDecimal acquiredQuotas) {
        if (isNull(this.totalQuotas))
            this.totalQuotas = BigDecimal.ZERO;

        this.totalQuotas = this.totalQuotas.add(acquiredQuotas);
    }
}
