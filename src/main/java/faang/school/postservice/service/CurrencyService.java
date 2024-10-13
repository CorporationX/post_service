package faang.school.postservice.service;

import faang.school.postservice.exception.api.APIConversionRatesException;
import faang.school.postservice.service.redis.CurrencyRedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyService {
    private final String conversionRatesFieldName;
    private final String currencyAPIUrl;

    private final WebClient.Builder webClientBuilder;
    private final CurrencyRedisService currencyRedisService;


    @Retryable(value = {APIConversionRatesException.class},
            maxAttemptsExpression = "${post.currency.scheduler.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.currency.scheduler.retry.backoff.delay}",
                    multiplierExpression = "${post.currency.scheduler.retry.backoff.multiplier}"))
    public void updateCurrencyRates() {
        webClientBuilder.build().get()
                .uri(currencyAPIUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .doOnNext(this::saveConversionRates)
                .doOnError(error -> log.error("Error fetching currency rates: {}", error.getMessage()))
                .subscribe();
    }

    @Recover
    public void recover(APIConversionRatesException ex) {
        log.error("ConversionRates API call failed.\n" +
                "All retry attempts failed: {}", ex.getMessage());

    }

    public Map<String, Object> getCurrencyRates() {
        return currencyRedisService.getCurrencyRates(conversionRatesFieldName);
    }

    private void saveConversionRates(Map<String, Object> response) {
        Map<String, Object> conversionRates = (Map<String, Object>) response.get(conversionRatesFieldName);
        currencyRedisService.saveCurrencyRates(conversionRatesFieldName, conversionRates);
    }

}
