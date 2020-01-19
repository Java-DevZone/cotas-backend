package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class QuotaService {

    public BigDecimal calculateQuotaFor(Asset asset) {

        return BigDecimal.ONE;
    }
}
