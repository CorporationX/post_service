package faang.school.postservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ResourceConfig {

    @Bean
    public ClassPathResource banWordResource() {
        return new ClassPathResource("ban-word.json");
    }
}
