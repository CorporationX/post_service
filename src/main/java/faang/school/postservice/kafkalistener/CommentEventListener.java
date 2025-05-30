package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.kafkaevents.CommentEvent;
import faang.school.postservice.exception.KafkaEventListenException;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    private final PostCacheService postCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String POSTS_HASH_KEY = "posts:";

    @Value("${spring.data.redis.comments.size}")
    private long commentsSize;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.comment}",
            containerFactory = "commentEventListenerContainerFactory"
    )
    public void handleEvent(CommentEvent event, Acknowledgment ack) {
        log.info("Получен комментарий {} к посту {}", event.content(), event.postId());
        try {
            if (postCached(event.postId()) && !commentCached(event.postId(), event.id())) {
                CommentDto comment = createCommentFromEvent(event);
                String commentKey = POSTS_HASH_KEY + event.postId() + ":comments";
                String zsetKey = POSTS_HASH_KEY + event.postId() + ":comments_sorted";
                long timestamp = event.createdAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                redisTemplate.opsForList().leftPush(commentKey, comment);
                redisTemplate.opsForZSet().add(zsetKey, comment.id(), timestamp);

                redisTemplate.opsForList().trim(commentKey, 0, commentsSize - 1);
                redisTemplate.opsForZSet().removeRange(zsetKey, -commentsSize - 1, -1);
                ack.acknowledge();
                log.info("Успешно добавил комментарий {} к посту {}", event.content(), event.postId());
            }
        } catch (Exception e) {
            log.error("Ошибка доставки ивента комментария {} для поста {}", event.id(), event.postId(), e);
            throw new KafkaEventListenException("Ошибка обработки ивента", e);
        }


    }

    private CommentDto createCommentFromEvent(CommentEvent event) {
        return CommentDto.builder()
                .createdAt(event.createdAt())
                .id(event.id())
                .authorId(event.commentAuthorId())
                .content(event.content())
                .postId(event.postId())
                .build();
    }

    private boolean postCached(Long postId) {
        Boolean exists = redisTemplate.opsForHash()
                .hasKey(POSTS_HASH_KEY, postId.toString());
        return Boolean.TRUE.equals(exists);
    }

    private boolean commentCached(Long postId, Long commentId) {
        String zsetKey = POSTS_HASH_KEY + postId + ":comments_sorted";
        boolean exists = redisTemplate.opsForZSet().score(
                zsetKey, commentId) != null;

        return  Boolean.TRUE.equals(exists);
    }
}
