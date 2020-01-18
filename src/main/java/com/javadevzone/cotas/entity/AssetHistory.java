package com.javadevzone.cotas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class AssetHistory {

    @Id
    private Long id;
    private BigDecimal value;
    private LocalDate dateTime;

    @OneToOne
    private Asset asset;

}

