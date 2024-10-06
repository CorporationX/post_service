package faang.school.postservice.service.post.async;

import faang.school.postservice.client.TextGearsClient;
import faang.school.postservice.dto.post.corrector.CorrectionResponseDto;
import faang.school.postservice.exception.correcter.TextGearsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.correcter.PostCorrecterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceAsyncImpl implements PostServiceAsync {

    private final PostRepository postRepository;
    private final PostCorrecterValidator postCorrecterValidator;
    private final TextGearsClient textGearsClient;

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
