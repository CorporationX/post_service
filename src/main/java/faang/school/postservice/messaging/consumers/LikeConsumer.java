package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostCacheDto;
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
public class LikeConsumer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(
            topics = "like-publication",
            groupId = "group"
    )
    public void listen(LikeDto likeDto) {
        log.info("PostConsumer has received: {}", likeDto);
        increaseLikeCounter(likeDto.getPostId());
    }

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void increaseLikeCounter(long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        PostCacheDto post = redisPost.getPostCacheDto();

        if (post != null) {
            long likeCounter = post.getLikeCounter() + 1;
            post.setLikeCounter(likeCounter);
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
