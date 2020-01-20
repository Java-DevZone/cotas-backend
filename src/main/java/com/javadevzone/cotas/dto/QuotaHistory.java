package com.javadevzone.cotas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotaHistory {

    private String ticket;
    private BigDecimal total;
    private List<QuotaHistoryData> dataList;

    public QuotaHistory(String ticket, List<QuotaHistoryData> dataList) {
        this.ticket = ticket;
        this.dataList = dataList;
    }

    @Value
    @Builder(toBuilder = true)
    @AllArgsConstructor
    public static class QuotaHistoryData {
        private LocalDate date;
        private BigDecimal quota;
        private BigDecimal value;
    }

}
