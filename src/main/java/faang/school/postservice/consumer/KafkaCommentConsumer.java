package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.dto.redis.CommentRedisDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class KafkaCommentConsumer extends AbstractKafkaConsumer<CommentEvent> {

    public KafkaCommentConsumer(ObjectMapper objectMapper,
                                RedisTemplate<String, Object> redisTemplate,
                                FeedCacheProperties feedCacheProperties) {
        super(objectMapper, redisTemplate, feedCacheProperties);
    }

    @KafkaListener(topics = "comments", groupId = "post-service-group")
    public void consume(ConsumerRecord<String, String> record) {
        consumeRecord(record, CommentEvent.class);
    }

    @Override
    protected void handleEvent(CommentEvent event) {
        String key = "post:" + event.getPostId();
        PostRedisDto post = (PostRedisDto) redisTemplate.opsForValue().get(key);
        if (post == null) {
            log.warn("Post {} not found in Redis for comment {}", event.getPostId(), event.getCommentId());
            return;
        }

        List<CommentRedisDto> comments = post.getComments();
        CommentRedisDto comment = CommentRedisDto.builder()
                .commentId(event.getCommentId())
                .authorId(event.getAuthorId())
                .text(event.getText())
                .createdAt(event.getCreatedAt())
                .build();

        comments.removeIf(c -> c.getCommentId().equals(comment.getCommentId()));
        comments.add(0, comment);

        int maxComments = feedCacheProperties.getMaxComments();
        if (comments.size() > maxComments) {
            post.setComments(comments.subList(0, maxComments));
        }

        redisTemplate.opsForValue().set(key, post, Duration.ofSeconds(feedCacheProperties.getTtlSeconds()));
    }

    @Override
    protected void logError(Exception e) {
        log.error("Error processing CommentEvent", e);
    }


}
