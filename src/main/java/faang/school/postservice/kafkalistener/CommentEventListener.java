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
        log.info("Получен комментарий {} к посту {}", event.getContent(), event.getPostId());
        try {
            if (postCached(event.getPostId()) && !commentCached(event.getPostId(), event.getId())) {
                CommentDto comment = createCommentFromEvent(event);
                String commentKey = POSTS_HASH_KEY + event.getPostId() + ":comments";
                String zsetKey = POSTS_HASH_KEY + event.getPostId() + ":comments_sorted";
                long timestamp = event.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                redisTemplate.opsForList().leftPush(commentKey, comment);
                redisTemplate.opsForZSet().add(zsetKey, comment.id(), timestamp);

                redisTemplate.opsForList().trim(commentKey, 0, commentsSize - 1);
                redisTemplate.opsForZSet().removeRange(zsetKey, -commentsSize - 1, -1);
                ack.acknowledge();
                log.info("Успешно добавил комментарий {} к посту {}", event.getContent(), event.getPostId());
            }
        } catch (Exception e) {
            log.error("Ошибка доставки ивента комментария {} для поста {}", event.getId(), event.getPostId(), e);
            throw new KafkaEventListenException("Ошибка обработки ивента", e);
        }


    }

    private CommentDto createCommentFromEvent(CommentEvent event) {
        return CommentDto.builder()
                .createdAt(event.getCreatedAt())
                .id(event.getId())
                .authorId(event.getCommentAuthorId())
                .content(event.getContent())
                .postId(event.getPostId())
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
