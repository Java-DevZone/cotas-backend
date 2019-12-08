package com.javadevzone.cotas.exceptions;

import com.javadevzone.cotas.entity.Fechamento;

public class ValoresDeFechamentoInvalidoException extends RuntimeException {

    public ValoresDeFechamentoInvalidoException(ArithmeticException e, Fechamento hoje, Fechamento ontem) {
        super(String.format("Não foi possível calcular a variação para os fechamentos %s e %s", hoje.getValor(), ontem.getValor()), e);
    }
}
