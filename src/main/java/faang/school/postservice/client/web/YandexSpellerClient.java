package faang.school.postservice.client.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;

@Component
public class YandexSpellerClient {
    public static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    @Value("${yandex-speller.base-host}")
    private String baseUrl;
    @Value("${yandex-speller.check-text-url}")
    private String checkTextUrl;

    private final WebClient.Builder webClientBuilder;

    public YandexSpellerClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String checkText(String text) {
        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();
        return webClient.get()
                .uri(checkTextUrl + text)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
