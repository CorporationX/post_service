package faang.school.postservice.config;


import faang.school.postservice.service.tools.YandexSpeller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class YandexSpellerConfig {
    @Value("${post.spell-corrector.url}")
    private String url;

    @Bean
    public YandexSpeller yandexSpeller() {
        return new YandexSpeller(url, restTemplate());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
