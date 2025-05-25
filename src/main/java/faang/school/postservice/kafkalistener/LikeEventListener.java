package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.kafkaevents.LikeFeedEvent;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventListener {
    private final PostCacheService postCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POSTS_HASH_KEY = "posts";

    @KafkaListener(topics = "${spring.data.kafka.topic.likes}",
                    containerFactory = "likeEventConsumerFactory")
    public void handleLikeEvent(LikeFeedEvent event) {
        try {
            String postKey = event.postId().toString();
            if (!postHashed(event.postId())) {
                log.warn("Пост не сохранен в кеш");
            } else {
                Long newCount = redisTemplate.opsForHash().increment(
                        POSTS_HASH_KEY, postKey + ".likes", 1L);
                log.info("Счетчик лайков для поста {} увеличен до: {}", event.id(), newCount);
            }
        } catch (Exception e) {
            log.error("Ошибка добавления лайка для поста {}", event.id(), e);
            throw new RuntimeException(e);
        }

    }

    private boolean postHashed(Long postId) {
        Boolean exists = redisTemplate.opsForHash()
                .hasKey(POSTS_HASH_KEY, postId.toString());
        return Boolean.TRUE.equals(exists);
    }

}
