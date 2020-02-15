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
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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

    public BigDecimal getWalletValue() {
        return this.quota.multiply(this.totalQuotas).setScale(6, RoundingMode.CEILING);
    }

    public void addTotalQuotas(BigDecimal acquiredQuotas) {
        if (isNull(this.totalQuotas))
            this.totalQuotas = BigDecimal.ZERO;

        this.totalQuotas = this.totalQuotas.add(acquiredQuotas);
    }
}
