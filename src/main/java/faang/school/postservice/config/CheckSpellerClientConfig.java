package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CheckSpellerClientConfig {
    @Bean("checkSpellerClient")
    public RestTemplate checkSpellerClient() {
        return new RestTemplate();
    }
}
