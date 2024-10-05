package faang.school.postservice.client;

import faang.school.postservice.config.TextGearsProperties;
import faang.school.postservice.dto.text.gears.TextGearsResponse;
import faang.school.postservice.exception.TextGearsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextGearsClient implements CorrecterTextClient<TextGearsResponse> {

    private final RestClient restClient;
    private final TextGearsProperties properties;

    @Override
    public TextGearsResponse correctText(String text) {
        log.info("Correct text: {}", text);
        return restClient.post()
                .uri(buildUri(text))
                .retrieve()
                .body(TextGearsResponse.class);
    }

    private URI buildUri(String text) {
        return UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(properties.getCorrect())
                .queryParam("text", text)
                .queryParam("key", properties.getApiKey())
                .build()
                .toUri();
    }
}
