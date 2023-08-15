package faang.school.postservice.service.postCorrecter;

import faang.school.postservice.client.BingSpellClient;
import faang.school.postservice.config.postCorrecter.BingSpellConfig;
import faang.school.postservice.dto.postCorrecter.FlaggedTokenDto;
import faang.school.postservice.dto.postCorrecter.SpellCheckDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private BingSpellConfig bingSpellConfig;
    private BingSpellClient bingSpellClient;

    @Async("bingSpellAsyncExecutor")
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 1000))
    public CompletableFuture<String> correctPostText(Post post) {
        String body = "Text=" + post.getContent();
        SpellCheckDto spellCheckDto = bingSpellClient.checkSpell(bingSpellConfig.getHeaders(), bingSpellConfig.getMode(), body).getBody();

        List<FlaggedTokenDto> flaggedTokenList = spellCheckDto.getFlaggedTokens();
        String text = post.getContent();
        for (FlaggedTokenDto flaggedToken : flaggedTokenList) {
            String token = flaggedToken.getToken();
            String correct = flaggedToken.getSuggestions().get(0).getSuggestion();
            text = text.replace(token, correct);
        }

        return CompletableFuture.completedFuture(text);
    }
}
