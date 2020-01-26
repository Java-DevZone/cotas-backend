package com.javadevzone.cotas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WalletHistory {

    @Id
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    private BigDecimal quota;
    private BigDecimal totalQuotas;
    private BigDecimal totalValue;

    private LocalDate registerDate;

    public BigDecimal getWalletValue() {
        return this.quota.multiply(this.totalQuotas);
    }
}
