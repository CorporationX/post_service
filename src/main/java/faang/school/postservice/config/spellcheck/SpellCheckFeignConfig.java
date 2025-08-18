package faang.school.postservice.config.spellcheck;

import faang.school.postservice.config.properties.SpellCheckApiProperties;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SpellCheckFeignConfig {

    private final SpellCheckApiProperties properties;

    @Bean
    public RequestInterceptor spellCheckRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("x-rapidapi-key", properties.apiKey());
            requestTemplate.header("x-rapidapi-host", properties.host());
            requestTemplate.header("Content-Type", "application/x-www-form-urlencoded");
        };
    }
}
