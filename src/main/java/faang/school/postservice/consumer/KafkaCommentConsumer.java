package faang.school.postservice.consumer;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Consumer для обработки событий создания комментариев из Kafka.
 * <p>
 * Класс получает события о созданных комментариях из Kafka топика,
 * сохраняет их в Redis с использованием атомарных операций и обеспечивает
 * ограничение количества комментариев на пост с заданным TTL.
 *
 * @author bozya
 * @since 27.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    @Value("${redis.max-comments}")
    private int maxComments;

    @Value("${redis.comments-ttl}")
    private int commentTtl;

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> atomicAddScript;

    @KafkaListener(topics = "${spring.kafka.topics.comments}")
    public void handleCommentCreated(ConsumerRecord<String, CommentCreatedEventAvro> consumerRecord) {
        CommentCreatedEventAvro event = consumerRecord.value();
        log.info("Получен комментарий {} для поста {}", event.getCommentId(), event.getPostId());

        try {
            atomicAddComment(event.getPostId(), event.getCommentId(), event.getCreatedAt().toEpochMilli());
            log.info("Комментарий {} сохранен для поста {}",
                    event.getCommentId(), event.getPostId());
        } catch (Exception e) {
            log.error("Комментарий {} не сохранен: {}", event.getCommentId(), e.getMessage());
        }
    }

    private void atomicAddComment(Long postId, Long commentId, Long timestamp) {
        String redisKey = "post:" + postId + ":comments";

        Long result = redisTemplate.execute(
                atomicAddScript,
                Collections.singletonList(redisKey),
                commentId,
                timestamp,
                maxComments
        );

        log.debug("После добавления комментария {} в пост {}: {} комментариев",
                commentId, postId, result);
    }
}