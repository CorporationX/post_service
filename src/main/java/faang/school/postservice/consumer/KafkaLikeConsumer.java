package faang.school.postservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final RedisTemplate<String, String> redisTemplate;

    @KafkaListener(topics = "${spring.kafka.topic.like.name}")
    public void consumeLike(long postId, long userId) {
        String postKey = "post:" + postId;
        String likesKey = "likes:" + postId;

        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(postKey))) {
                boolean isNewLike = redisTemplate.opsForSet().add(likesKey, String.valueOf(userId)) == 1L;

                if (isNewLike) {
                    redisTemplate.opsForHash().increment(postKey, "likeCount", 1L);
                    log.info("New like added for post {} by user {}", postId, userId);
                }
                else {
                    log.info("Like already exists for post {} by user {}", postId, userId);
                }
            }
            else {
                log.warn("Post {} not found in Redis, skipping like from user {}", postId, userId);
            }
        } catch (Exception e) {
            log.error("Error processing like.", e);
        }
    }
}
