package faang.school.postservice.service.spellcheck;

import faang.school.postservice.client.SpellCheckClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.language.LanguageDetectionService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.spellcheck.PostSpellCheckValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncPostSpellCheckerService {

    private final PostService postService;
    private final PostSpellCheckValidator validator;
    private final LanguageDetectionService languageService;
    private final SpellCheckClient spellCheckClient;

    @Async("postCorrectionExecutor")
    public void correctPostBatchAsync(List<Post> batch) {
        log.info("Processing batch of {} posts", batch.size());
        for (Post post : batch) {
            correctPost(post);
        }
    }

    private void correctPost(Post post) {
        if (!validator.hasValidText(post)) {
            return;
        }
        String language = languageService.detectLanguageCode(post.getContent());
        String corrected = getCorrectedContentWithRetry(post.getContent(), language);
        postService.updatePostContent(post, corrected);
        log.info("Corrected post ID {}", post.getId());
    }

    @Retryable(
            retryFor = FeignException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public String getCorrectedContentWithRetry(String content, String lang) {
        return spellCheckClient.checkText(content, lang).getCorrected();
    }

    @Recover
    public String recoverFromSpellCheckFailure(FeignException ex, String content, String lang) {
        log.error("Failed to correct text after 3 attempts. Reason: {}", ex.getMessage());
        return content;
    }
}
