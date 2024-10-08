package faang.school.postservice.config.corrector;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Setter
@Configuration
@RequiredArgsConstructor
public class CorrectorClientConfig {
    private final CorrectorClientParams params;

    @Bean
    public WebClient getCorrectorClient() {
        return WebClient.builder()
                .defaultHeader("Authorization", "Basic " + params.getApiKey())
                .baseUrl(params.getUrl())
                .build();
    }
}
