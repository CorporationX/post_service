package faang.school.postservice.scheduler;

import faang.school.postservice.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.fetcher.cron}")
    public void fetchCurrencyRate() {
        currencyService.updateCurrencyRates();
    }
}