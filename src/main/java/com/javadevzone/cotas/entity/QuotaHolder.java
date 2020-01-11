package com.javadevzone.cotas.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
public class QuotaHolder {

    @Id
    private Long id;
    private String name;
    private LocalDate optInAt;
    private LocalDate optOutAt;

    @OneToMany(mappedBy = "quotaHolder")
    private Set<Investment> investments;

}
