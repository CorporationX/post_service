package faang.school.postservice.сonsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.redis.CommentRedisDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.properties.FeedCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final FeedCacheProperties feedCacheProperties;

    @KafkaListener(topics = "comments", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        try {
            CommentEvent event = objectMapper.readValue(record.value(), CommentEvent.class);
            String key = "post:" + event.getPostId();

            PostRedisDto post = (PostRedisDto) redisTemplate.opsForValue().get(key);
            if (post == null) {
                log.warn("Пост {} не найден в Redis для комментария {}", event.getPostId(), event.getCommentId());
                return;
            }

            CommentRedisDto comment = CommentRedisDto.builder()
                    .commentId(event.getCommentId())
                    .authorId(event.getAuthorId())
                    .text(event.getText())
                    .createdAt(event.getCreatedAt())
                    .build();

            post.getComments().removeIf(c -> c.getCommentId().equals(comment.getCommentId()));
            post.getComments().add(0, comment);

            int maxComments = feedCacheProperties.getMaxComments();

            if (post.getComments().size() > maxComments) {
                post.setComments(post.getComments().subList(0, maxComments));
            }

            redisTemplate.opsForValue().set(key, post, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));

        } catch (Exception e) {
            log.error("Ошибка обработки KafkaCommentConsumer", e);
        }
    }
}
