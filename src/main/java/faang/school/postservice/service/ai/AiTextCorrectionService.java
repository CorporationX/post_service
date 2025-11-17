package faang.school.postservice.service.ai;

import faang.school.postservice.client.AIClient;
import faang.school.postservice.exception.AiServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiTextCorrectionService {

    private final AIClient aiClient;

    @Retryable(
            retryFor = {HttpServerErrorException.class, AiServiceException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String correct(String text) {
        try {
            return aiClient.correctText(text);
        } catch (HttpServerErrorException e) {
            log.warn("AI Speller unavailable (HTTP {}), retrying...", e.getStatusCode());
            throw e;
        } catch (AiServiceException e) {
            log.warn("AI correction failed: {}, retrying...", e.getMessage());
            throw e;
        }
    }

    @Recover
    public String recover(HttpServerErrorException e, String text) {
        log.error("AI Speller unavailable after retries for text: {}", text, e);
        return text;
    }

    @Recover
    public String recover(AiServiceException e, String text) {
        log.error("AI correction failed after retries for text: {}", text, e);
        return text;
    }
}