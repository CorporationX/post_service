package faang.school.postservice.client.web;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Locale;

@Service
public class YandexSpellerClient {
    public static final Locale RUSSIAN_LOCALE = new Locale("ru", "RU");
    private static final String BASE_URL = "https://speller.yandex.net/services/spellservice.json";
    private static final String CHECK_TEXT_URL = "/checkText?text=";

    private final WebClient.Builder webClientBuilder;

    public YandexSpellerClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String checkText(String text) {
        WebClient webClient = webClientBuilder.baseUrl(BASE_URL).build();
        return webClient.get()
                .uri(CHECK_TEXT_URL + text)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
