package com.javadevzone.cotas.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Investment {

    @Id
    private Long id;
    private BigDecimal value;
    private LocalDateTime dateTime;

    @ManyToOne
    private QuotaHolder quotaHolder;

}
