package com.javadevzone.cotas.exceptions;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException() {
        super("A Wallet não foi encontrada");
    }

}
