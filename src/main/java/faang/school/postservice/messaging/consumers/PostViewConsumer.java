package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
@RequiredArgsConstructor
public class PostViewConsumer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(
            topics = "post-view",
            groupId = "group"
    )
    public void listen(PostViewEvent postView) {
        log.info("PostConsumer has received: {}", postView);
        increasePostView(postView.getPostId());
        log.info("View: {} was successfully added to the post", postView);
    }

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void increasePostView(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        PostCacheDto post = redisPost.getPostCacheDto();

        if (post != null) {
            long currentViews = post.getViews();
            long updatedViews = currentViews + 1;

            post.setViews(updatedViews);
            redisPost.setPostCacheDto(post);

            try {
                redisPostRepository.save(redisPost);
            } catch (OptimisticLockingFailureException ex) {
                log.error("Error occurred while updating the like counter for post with ID: " + postId);
                log.error(ex.getMessage());
            }
        }
    }
}
