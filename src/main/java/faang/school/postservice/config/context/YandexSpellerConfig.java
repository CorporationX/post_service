package faang.school.postservice.config.context;


import faang.school.postservice.service.YandexSpeller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YandexSpellerConfig {
    //todo from yaml
    private String url;

    @Bean
    public YandexSpeller yandexSpeller() {
        return new YandexSpeller(url);
    }
}
