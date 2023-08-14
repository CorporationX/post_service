package faang.school.postservice.service.postCorrecter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.BingSpellCheckingClient;
import faang.school.postservice.config.BingSpellCheckingConfig.BingSpellCheckingConfig;
import faang.school.postservice.dto.postCorrecter.FlaggedToken;
import faang.school.postservice.dto.postCorrecter.PostCorrecterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TextCorrecter {

    private final BingSpellCheckingClient bingSpellCheckingClient;
    private final BingSpellCheckingConfig bingSpellCheckingConfig;

    @Async("bingSpellAsyncExecutor")
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000))
    public CompletableFuture<String> getCorrectText(String text) throws JsonProcessingException {
        String body = "Text=" + text;

        var stringHttpResponse = bingSpellCheckingClient
                .makeTextCorrect(bingSpellCheckingConfig.getHeaders(), bingSpellCheckingConfig.getMode(), body);

        ObjectMapper objectMapper = new ObjectMapper();
        PostCorrecterDto postCorrecterDto1 = objectMapper
                .readValue(stringHttpResponse.getBody(), PostCorrecterDto.class);

        List<FlaggedToken> flaggedTokens = postCorrecterDto1.getFlaggedTokens();
        for (FlaggedToken flaggedToken : flaggedTokens) {
            String incorrectWord = flaggedToken.token;
            String suggestionWord = flaggedToken.suggestions.get(0).suggestion;
            text = text.replace(incorrectWord, suggestionWord);
        }
        return CompletableFuture.completedFuture(text);
    }
}
