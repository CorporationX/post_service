package faang.school.postservice.client;

import faang.school.postservice.dto.post.grammar.TextGearsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TextGearsClient {

    @Value("${textgears.api.key:default_test_key}")
    private String apiKey;

    private final WebClient textGearsWebClient;

    @Retryable(
            retryFor = {RuntimeException.class,
                    WebClientResponseException.class,
                    WebClientRequestException.class,
                    ResourceAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2
            )
    )
    public Mono<String> correctText(String text) {
        return sendRequest(text)
                .map(this::validateResponse)
                .map(responseDto -> applyCorrections(text, responseDto));
    }

    private Mono<TextGearsResponseDto> sendRequest(String text) {
        return textGearsWebClient.post()
                .uri("/grammar")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("text", text)
                        .with("language", "ru-RU")
                        .with("key", apiKey))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(TextGearsResponseDto.class);
    }

    private Mono<? extends Throwable> handleErrorResponse(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> Mono.error(new RuntimeException("TextGears API error: " + body)));
    }

    private TextGearsResponseDto validateResponse(TextGearsResponseDto responseDto) {
        if (!responseDto.status() || responseDto.response() == null) {
            throw new RuntimeException("Ошибка при обращении к TextGears API");
        }
        return responseDto;
    }

    private String applyCorrections(String originalText, TextGearsResponseDto responseDto) {
        if (responseDto.response() == null || responseDto.response().errors() == null) {
            return originalText;
        }

        StringBuilder correctedText = new StringBuilder(originalText);

        responseDto.response().errors().stream()
                .filter(err -> err.betterVersion() != null && !err.betterVersion().isEmpty())
                .sorted((a, b) -> Integer.compare(b.offset(), a.offset()))
                .forEach(err -> {
                    int start = err.offset();
                    int end = start + err.length();
                    if (start >= 0 && end <= correctedText.length() && start <= end) {
                        correctedText.replace(start, end, err.betterVersion().get(0));
                    }
                });
        return correctedText.toString();
    }
}
