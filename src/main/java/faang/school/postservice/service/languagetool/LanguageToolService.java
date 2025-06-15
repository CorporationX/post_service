package faang.school.postservice.service.languagetool;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
@RequiredArgsConstructor
public class LanguageToolService {
    private final RestTemplate restTemplate;
    private final Executor customExecutor;

    @Value("${app.correction.url}")
    private String url;

    public CompletableFuture<LanguageToolResponse> checkText(String originalText, String language) {
        HashMap<String, String> params = new HashMap<>();
        params.put("text", originalText);
        params.put("language", language);

        log.debug("Sending request to LanguageTool for text: {}", originalText);
        return CompletableFuture
                .supplyAsync(
                        () -> restTemplate.getForObject(url, LanguageToolResponse.class, params),
                        customExecutor)
                .exceptionally(e -> {
                    log.error("External API request failed", e);
                    throw new RestClientException("Failed to check text with LanguageTool");
                });
    }
}
