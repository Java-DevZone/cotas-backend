package com.javadevzone.cotas.entity;

import com.javadevzone.cotas.entity.enums.TipoAtivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ativo {

    private String codigo;
    private TipoAtivo tipo;

}
