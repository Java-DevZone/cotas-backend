package com.javadevzone.cotas.entity;

import com.javadevzone.cotas.entity.enums.TipoAtivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Ativo {

    @Id
    private String codigo;

    @Enumerated(EnumType.STRING)
    private TipoAtivo tipo;

    private Integer quantidade;

}
