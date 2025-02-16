package faang.school.postservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "rest-template.config")
public class RestTemplateConfig {
    private int connectTimeout;
    private int readTimeout;

    @Bean(name = "PostServiceRestTemplate")
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();

    }
}
