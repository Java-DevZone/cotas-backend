package com.javadevzone.cotas.entity;

import com.javadevzone.cotas.entity.enums.TipoAtivo;
import lombok.Data;

@Data
public class Ativo {

    private String codigo;
    private TipoAtivo tipo;

}
