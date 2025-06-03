package faang.school.postservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private static final Long INCREMENT_VALUE = 1L;
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(topics = "${spring.kafka.topic.like.name}")
    public void consumeLike(long postId, long userId) {
        String postKey = "post:" + postId;
        String likesKey = "likes:" + postId;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(postKey))) {
            log.warn("Post {} not found in Redis, skipping like from user {}", postId, userId);
            return;
        }

        try {
            Long likeAdded = redisTemplate.opsForSet().add(likesKey, String.valueOf(userId));
            boolean isNewLike = Objects.equals(likeAdded, INCREMENT_VALUE);

            if (isNewLike) {
                redisTemplate.opsForHash().increment(postKey, "likeCount", INCREMENT_VALUE);
                log.info("New like added for post {} by user {}", postId, userId);
            } else {
                log.info("Like already exists for post {} by user {}", postId, userId);
            }
        } catch (Exception e) {
            log.error("Error processing like.", e);
        }
    }
}
