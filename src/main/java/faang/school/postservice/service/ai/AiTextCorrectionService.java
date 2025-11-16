package faang.school.postservice.service.ai;

import faang.school.postservice.client.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiTextCorrectionService {

    private final AIClient aiClient;

    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String correct(String text) {
        return aiClient.correctText(text);
    }
}