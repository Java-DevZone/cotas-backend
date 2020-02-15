package com.javadevzone.cotas.services;

import com.javadevzone.cotas.entity.Wallet;
import com.javadevzone.cotas.entity.WalletHistory;
import com.javadevzone.cotas.repository.WalletHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.CEILING;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuotaService {

    private final WalletHistoryRepository walletHistoryRepository;
    private final InvestmentService investmentService;

    public WalletHistory calculateQuotaValue(Wallet wallet, LocalDate date) {
        WalletHistory walletHistory = walletHistoryRepository.findFirstByWalletAndRegisterDateIsBeforeOrderByRegisterDateDesc(wallet, date)
                .orElse(WalletHistory.builder().wallet(wallet).quota(ONE).totalQuotas(ZERO).build());

        if (walletHistory.getWalletValue().compareTo(BigDecimal.ZERO) == 0)
            return new WalletHistory(null, wallet, ONE, walletHistory.getTotalQuotas(), date);

        BigDecimal profitability = investmentService.calculateInvestmentsProfitability(walletHistory, date);
        BigDecimal newQuotaValue = calculateQuota(walletHistory.getQuota(), profitability);

        return new WalletHistory(null, wallet, newQuotaValue, walletHistory.getTotalQuotas(), date);
    }

    private BigDecimal calculateQuota(BigDecimal actualQuota, BigDecimal profitability) {
        return actualQuota.add(actualQuota.multiply(profitability).setScale(6, CEILING));
    }

}
