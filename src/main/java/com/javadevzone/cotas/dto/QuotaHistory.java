package com.javadevzone.cotas.dto;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
public class QuotaHistory {

    private String ticket;
    private List<QuotaHistoryData> dataList;

    @Value
    public static class QuotaHistoryData {
        private LocalDate date;
        private BigDecimal quota;
        private BigDecimal value;
    }

}
