package faang.school.postservice.config.correct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

    @Value("${yandex-speller.url}")
    private String yandexSpellerUrl;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(yandexSpellerUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        return webClientBuilder.uriBuilderFactory(factory).build();
    }
}