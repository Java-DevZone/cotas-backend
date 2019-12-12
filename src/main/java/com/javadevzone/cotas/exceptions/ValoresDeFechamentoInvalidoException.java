package com.javadevzone.cotas.exceptions;

import java.math.BigDecimal;

public class ValoresDeFechamentoInvalidoException extends RuntimeException {

    public ValoresDeFechamentoInvalidoException(ArithmeticException e, BigDecimal hoje, BigDecimal ontem) {
        super(String.format("Não foi possível calcular a variação para os fechamentos %s e %s", hoje, ontem), e);
    }
}
