package com.endava.insuranceapp.application.scheduler;

import com.endava.insuranceapp.application.service.ExchangeRateRefreshService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateJob {

    private final ExchangeRateRefreshService refreshService;

    public ExchangeRateJob(ExchangeRateRefreshService refreshService) {
        this.refreshService = refreshService;
    }

    // Runs daily at 02:05 in Bucharest time
//    @Scheduled(cron = "0 5 2 * * *", zone = "Europe/Bucharest")
//    public void refreshRates() {
//        refreshService.refreshActiveCurrencies();
//    }

    @Scheduled(fixedDelay = 20000)
    public void refreshRates() {
        refreshService.refreshActiveCurrencies();
    }
}