package com.javadevzone.cotas.exceptions;

import com.javadevzone.cotas.entity.Asset;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static java.lang.String.format;

public class AssetHistoryNotFoundException extends RuntimeException {

    public AssetHistoryNotFoundException(@NotNull Asset asset) {
        super(format("Não foi possível encontra um Asset History para o Ativo %s", asset));
    }
}
