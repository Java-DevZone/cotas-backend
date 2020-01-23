package com.javadevzone.cotas.exceptions;

import com.javadevzone.cotas.entity.Asset;

import javax.validation.constraints.NotNull;

import static java.lang.String.format;

public class AssetNotFoundException extends RuntimeException {

    public AssetNotFoundException(@NotNull Asset asset) {
        super(format("Não foi possível encontra a Asset com Ticket %s", asset.getTicket()));
    }
}
