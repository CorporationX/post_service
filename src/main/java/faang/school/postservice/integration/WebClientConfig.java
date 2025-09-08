package faang.school.postservice.integration;


import faang.school.postservice.integration.project.config.ProjectClientProperties;
import faang.school.postservice.integration.user.config.UserClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(value = {UserClientProperties.class, ProjectClientProperties.class})
public class WebClientConfig {

    @Bean("userWebClient")
    public WebClient userWebClient(UserClientProperties properties) {
        return WebClient.builder()
                .baseUrl("http://" + properties.host() + ":" + properties.port())
                .build();
    }

    @Bean("projectWebClient")
    public WebClient projectWebClient(ProjectClientProperties properties) {
        return WebClient.builder()
                .baseUrl("http://" + properties.host() + ":" + properties.port())
                .build();
    }
}
