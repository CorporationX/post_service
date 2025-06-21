package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.kafkaevents.LikeFeedEvent;
import faang.school.postservice.exception.KafkaEventListenException;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeEventListener {
    private final PostCacheService postCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String POSTS_HASH_KEY = "posts:";
    private static final String GLOBAL_FEED_KEY = "feed:top";

    @KafkaListener(topics = "${spring.data.kafka.topic.likes}",
                    containerFactory = "likeEventListenerContainerFactory")
    public void handleLikeEvent(LikeFeedEvent event, Acknowledgment acknowledgment) {
        log.debug("Обработка лайка для поста {} (идентификатор события: {})",
                event.getPostId(), event.getId());
        try {
            String postKey = event.getPostId().toString();
            if (!postCashed(event.getId())) {
                log.warn("Пост не сохранен в кеш");
            } else {
                Long newCount = redisTemplate.opsForHash().increment(
                        POSTS_HASH_KEY + postKey, "likes", 1L);
                log.info("Счетчик лайков для поста {} увеличен до: {}", event.getPostId(), newCount);
                acknowledgment.acknowledge();
                stringRedisTemplate.opsForZSet().add(GLOBAL_FEED_KEY, postKey, newCount);
            }
        } catch (Exception e) {
            log.error("Ошибка добавления лайка для поста {}", event.getPostId(), e);
            throw new KafkaEventListenException("Ошибка обработки ивента", e);
        }

    }

    private boolean postCashed(Long postId) {
        Boolean exists = redisTemplate.opsForHash()
                .hasKey(POSTS_HASH_KEY, postId.toString());
        return Boolean.TRUE.equals(exists);
    }

}
