package faang.school.postservice.client;

import faang.school.postservice.config.correcter.TextGearsProperties;
import faang.school.postservice.model.dto.corrector.CorrectionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class TextGearsClient {

    private final WebClient webClient;
    private final TextGearsProperties textGearsProperties;

    public CorrectionResponseDto correctText(String text) {
        return webClient.get()
                .uri(uriBuilder -> buildUri(text))
                .retrieve()
                .bodyToMono(CorrectionResponseDto.class)
                .block();
    }

    private URI buildUri(String text) {
        return UriComponentsBuilder
                .fromUriString(textGearsProperties.getBaseUrl())
                .path(textGearsProperties.getPathSegment())
                .queryParam("text", text)
                .queryParam("language", textGearsProperties.getLanguageCode())
                .queryParam("key", textGearsProperties.getApiKey())
                .build()
                .toUri();
    }
}
