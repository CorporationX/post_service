package faang.school.postservice.service.impl.post.async;

import faang.school.postservice.client.TextGearsClient;
import faang.school.postservice.dto.post.corrector.CorrectionResponseDto;
import faang.school.postservice.exception.correcter.TextGearsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostServiceAsync;
import faang.school.postservice.validator.correcter.PostCorrecterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceAsyncImpl implements PostServiceAsync {
    private final PostRepository postRepository;
    private final PostCorrecterValidator postCorrecterValidator;
    private final TextGearsClient textGearsClient;

    @Async("fixedThreadPool")
    public void publishScheduledPostsAsyncInBatch(List<Post> posts) {
       posts.forEach(post -> {
           post.setPublished(true);
           post.setPublishedAt(LocalDateTime.now());
       });
       postRepository.saveAll(posts);
    }

    @Override
    @Retryable(
            retryFor = TextGearsException.class, maxAttemptsExpression = "${post.correcter.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.correcter.retry.delay}",
                    multiplierExpression = "${post.correcter.retry.multiplier}")
    )
    @Async("fixedThreadPool")
    @Transactional
    public void correctUnpublishedPostsByBatches(List<Post> posts) {
        posts.forEach(post -> {
            try {
                CorrectionResponseDto response = textGearsClient.correctText(post.getContent());
                postCorrecterValidator.isCorrectResponse(response);
                String corrected = response.response().corrected();
                post.setContent(corrected);
            } catch (TextGearsException e) {
                log.error("Failed to correct", e);
            }
        });
        postRepository.saveAll(posts);
    }
}
