package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {
    @Value("${post.currency.api-response-json-field-name}")
    private String conversionRatesFieldName;

    @Value("${post.currency.api-url}")
    private String currencyAPIUrl;

    @Value("${post.currency.api-key}")
    private String currencyAPIKey;

    @Value("${post.currency.conversion-rate-usd}")
    private String conversionRateUsd;


    @Bean
    public String currencyAPIUrl() {
        return generateUrl(currencyAPIUrl, currencyAPIKey, conversionRateUsd);
    }
    @Bean
    public String conversionRatesFieldName() {
        return conversionRatesFieldName;
    }

    private String generateUrl(String apiUrl, String apiKey, String conversionRateUsd) {
        if (apiKey == null || conversionRateUsd == null || apiUrl == null) {
            throw new IllegalArgumentException("Missing required properties for currency service: apiKey, conversionRateUsd, apiUrl");
        }
        System.out.println(conversionRatesFieldName);
        return apiUrl.replace("{key}", apiKey).replace("{conversion-rate}", conversionRateUsd);
    }

}
