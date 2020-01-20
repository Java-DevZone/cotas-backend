package com.javadevzone.cotas.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne
    @NotNull
    private Asset asset;

    @ManyToOne
    private QuotaHolder quotaHolder;

    @ManyToOne
    private Wallet wallet;

    public BigDecimal getInvestmentTotal() {
        return this.value.multiply(new BigDecimal(quantity));
    }
}
