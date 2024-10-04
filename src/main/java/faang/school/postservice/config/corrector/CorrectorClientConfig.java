package faang.school.postservice.config.corrector;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@Setter
@ConfigurationProperties(prefix = "services.grammar-checker")
public class CorrectorClientConfig {
    private String url;

    @Bean
    public WebClient getCorrectorClient() {
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
