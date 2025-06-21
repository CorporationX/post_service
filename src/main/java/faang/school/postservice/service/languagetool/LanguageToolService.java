package faang.school.postservice.service.languagetool;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class LanguageToolService {
    private final RestTemplate restTemplate;
    private final ThreadPoolTaskExecutor executor;

    @Value("${app.correction.url}")
    private String url;

    @Value("${app.correction.language}")
    private String language;

    @Retryable(
            retryFor = {RestClientResponseException.class, RestClientException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<LanguageToolResponse> checkText(String originalText) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("text", originalText);
        formData.add("language", language);

        log.debug("Sending request to LanguageTool for text: {}", originalText);
        return CompletableFuture
                .supplyAsync(
                        () -> restTemplate.postForObject(url, formData, LanguageToolResponse.class),
                        executor)
                .exceptionally(e -> {
                    log.error("External API request failed", e);
                    throw new RestClientException("Failed to check text with LanguageTool");
                });
    }
}
