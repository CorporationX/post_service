package faang.school.postservice.client;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ai.spellcheck-url}")
    private String spellcheckUrl;

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String correctText(String text) {
        try {
            String url = spellcheckUrl + "?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

            ResponseEntity<String> response =
                    restTemplate.getForEntity(url, String.class);

            String body = response.getBody();

            if (body == null || body.trim().isEmpty()) {
                return text;
            }

            JsonNode root = objectMapper.readTree(body);

            if (!root.isArray() || root.isEmpty()) {
                return text;
            }

            String corrected = applyCorrections(text, root);

            log.info("Corrected text: {}", corrected);
            return corrected;

        } catch (Exception e) {
            log.warn("Yandex Speller request failed: {}", e.getMessage());
            return text;
        }
    }

    @Recover
    public String recover(Exception e, String originalText) {
        log.error("Spellcheck failed after retries", e);
        return originalText;
    }

    private String applyCorrections(String text, JsonNode errors) {
        String corrected = text;

        for (JsonNode error : errors) {
            String wrong = error.get("word").asText();
            JsonNode s = error.get("s");

            if (s.isArray() && !s.isEmpty()) {
                String suggestion = s.get(0).asText();
                corrected = corrected.replace(wrong, suggestion);
            }
        }

        return corrected;
    }
}
