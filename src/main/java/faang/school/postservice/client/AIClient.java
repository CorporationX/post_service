package faang.school.postservice.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;

    @Value("${ai.spellcheck-url}")
    private String spellcheckUrl;

    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String correctText(String text) {
        try {
            HttpEntity<String> request = buildRequest(text);

            String corrected = restTemplate.postForObject(
                    spellcheckUrl,
                    request,
                    String.class
            );
            if (corrected == null) {
                throw new IllegalStateException("Empty AI response");
            }

            return corrected;

        } catch (Exception e) {
            log.warn("AI request failed, retrying: {}", e.getMessage());
            throw e;
        }
    }

    @Recover
    public String recover(Exception e, String text) {
        log.error("AI correction failed after retries. Returning original text", e);
        return text;
    }

    private HttpEntity<String> buildRequest(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(text, headers);
    }
}
