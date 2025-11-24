package faang.school.postservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${textgears.base-url}")
    private String baseUrl;

    @Bean
    public WebClient textGearsWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
