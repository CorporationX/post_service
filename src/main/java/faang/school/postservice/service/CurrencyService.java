package faang.school.postservice.service;

import faang.school.postservice.exception.api.APIConversionRatesException;
import faang.school.postservice.service.redis.CurrencyRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class CurrencyService {
    private final String apiUrl;
    private final String apiKey;
    private final String conversionRateUsd;
    private final String conversionRatesFieldName;
    private final WebClient webClient;

    private final CurrencyRedisService currencyRedisService;

    public CurrencyService(@Value("${post.currency.api-url}") String apiUrl,
                           @Value("${post.currency.api-key}") String apiKey,
                           @Value("${post.currency.conversion-rate-usd}") String conversionRateUsd,
                           @Value("${post.currency.api-response-json-field-name}") String conversionRatesFieldName,
                           WebClient.Builder webClientBuilder,
                           CurrencyRedisService currencyRedisService) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.conversionRateUsd = conversionRateUsd;
        this.conversionRatesFieldName = conversionRatesFieldName;
        this.apiUrl = generatedUrl(apiUrl, apiKey, conversionRateUsd);
        this.currencyRedisService = currencyRedisService;
    }


    @Retryable(value = {APIConversionRatesException.class},
            maxAttemptsExpression = "${post.currency.scheduler.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.currency.scheduler.retry.backoff.delay}",
                    multiplierExpression = "${post.currency.scheduler.retry.backoff.multiplier}"))
    public void updateCurrencyRates() {
        webClient.get()
                .uri(apiUrl)
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
        return currencyRedisService.getCurrencyRates();
    }

    private void saveConversionRates(Map<String, Object> response) {
        Map<String, Object> conversionRates = (Map<String, Object>) response.get(conversionRatesFieldName);
        currencyRedisService.saveCurrencyRates(conversionRatesFieldName, conversionRates);
    }

    private String generatedUrl(String apiUrl, String apiKey, String conversionRateUsd) {
        if (apiKey == null || conversionRateUsd == null || apiUrl == null) {
            log.error("Missing required properties: apiKey={}, conversionRateUsd={}, apiUrl={}", apiKey, conversionRateUsd, apiUrl);
            throw new IllegalArgumentException("Missing required properties for currency service: apiKey, conversionRateUsd, apiUrl");
        }
        return apiUrl.replace("{key}", apiKey).replace("{conversion-rate}", conversionRateUsd);
    }

}
