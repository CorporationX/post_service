package faang.school.postservice.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient textGearsWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.textgears.com")
                .build();
    }
}
