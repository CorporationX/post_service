package faang.school.postservice.service.hash;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.publisher.HeatFeedEventPublisher;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncFeedHeaterService {
    private final PostEventMapper postEventMapper;
    private final PostService postService;
    private final HeatFeedEventPublisher heatFeedEventPublisher;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void publishBatch(List<Long> userBatch) {
        userBatch.forEach(userId -> {
            List<PostDto> publishedPostsByUser = postService.getPublishedPostsByUser(userId);
            publishedPostsByUser.forEach(PostDto -> {
                heatFeedEventPublisher.publish(postEventMapper.toPostEvent(PostDto));
            });
        });
    }
}
