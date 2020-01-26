package com.javadevzone.cotas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class QuotaHistory {

    private String ticket;
    private BigDecimal quotaTotal;
    private Long quantity;
    private List<QuotaHistoryData> dataList;

    private QuotaHistoryData lastHistoryData;

    public QuotaHistory() {
        this.quantity = 0L;
        this.quotaTotal = BigDecimal.ZERO;
    }

    public QuotaHistory(String ticket, List<QuotaHistoryData> dataList) {
        this();
        this.ticket = ticket;
        this.dataList = dataList;
    }

    public void addQuantity(Long quantity) {
        this.quantity += quantity;
    }

    public void addQuotaTotal(BigDecimal newQuotas) {
        this.quotaTotal = this.quotaTotal.add(newQuotas);
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
