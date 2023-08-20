package faang.school.postservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextGearsAPIServiceConfig {
    @Bean
    @ConfigurationProperties(prefix = "ai.api")
    public AIApiConfig aiApiConfig() {
        return new AIApiConfig();
    }
}
