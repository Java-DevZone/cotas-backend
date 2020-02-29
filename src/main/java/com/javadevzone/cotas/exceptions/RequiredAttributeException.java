package com.javadevzone.cotas.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredAttributeException extends RuntimeException {

    public RequiredAttributeException(String mensagem) {
        super(mensagem);
    }
}
