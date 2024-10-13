package faang.school.postservice.service;

import faang.school.postservice.service.redis.CurrencyRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Service
public class CurrencyService {
    private final String apiUrl;
    private final String apiKey;
    private final String conversionRateUsd;
    private final WebClient webClient;
    private final CurrencyRedisService currencyRedisService;

    public CurrencyService(@Value("${currency.api-url}") String apiUrl,
                           @Value("${currency.api-key}") String apiKey,
                           @Value("${currency.conversion-rate-usd}") String conversionRateUsd,
                           WebClient.Builder webClientBuilder,
                           CurrencyRedisService currencyRedisService) {
        this.webClient = webClientBuilder.build();
        this.apiKey = apiKey;
        this.conversionRateUsd = conversionRateUsd;
        this.apiUrl = generatedUrl(apiUrl,apiKey,conversionRateUsd);
        this.currencyRedisService = currencyRedisService;
    }


    @Retryable(value = {Exception.class},
            maxAttemptsExpression = "${currency.fetcher.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${currency.fetcher.retry.backoff}"))
    public void updateCurrencyRates() {
        webClient.get()
                .uri(apiUrl)
                .exchangeToMono(res -> {
                    if (res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        });
                    } else if (res.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Client Error: can't fetch currency rates"));
                    } else if (res.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("Server Error: can't fetch currency rates"));
                    } else {
                        return res.createError();
                    }
                })
                .doOnNext(response ->{
                    currencyRedisService.saveCurrencyRates((Map<String, Object>) response.get("conversion_rates"));
                })
                .doOnError(error -> log.error("Error fetching currency rates: {}", error.getMessage()))
                .subscribe();
    }

    public Map<String, Object> getCurrencyRates() {
        return currencyRedisService.getCurrencyRates();
    }



    private String generatedUrl(String apiUrl, String apiKey, String conversionRateUsd) {
        if (apiKey == null || conversionRateUsd == null || apiUrl == null) {
            log.error("Missing required properties: apiKey={}, conversionRateUsd={}, apiUrl={}", apiKey, conversionRateUsd, apiUrl);
            throw new IllegalArgumentException("Missing required properties for currency service: apiKey, conversionRateUsd, apiUrl");
        }
        return apiUrl.replace("{key}", apiKey).replace("{conversion-rate}", conversionRateUsd);
    }

}
