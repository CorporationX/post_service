package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${post.comments.max-size}")
    private Integer maxComments;

    @KafkaListener(topics = "${kafka.topic.comment}",
                   groupId = "comments-group"
    )
    public void listen(String message, Acknowledgment ack) {

        try {
            CommentEventDto event =
                    objectMapper.readValue(message, CommentEventDto.class);

            String key = "post:" + event.postId() + ":comments";

            redisTemplate.opsForStream().add(
                    StreamRecords.mapBacked(
                                    Map.of(
                                            "commentId", event.commentId().toString(),
                                            "authorId", event.commentAuthorId().toString(),
                                            "text", event.commentText()
                                    )
                            )
                            .withStreamKey(key)
                            .withId(RecordId.of(event.commentId().toString()))
            );

            redisTemplate.opsForStream().trim(key, maxComments, true);

            ack.acknowledge();

        } catch (JsonProcessingException e) {

            log.error("Invalid JSON message: {}", message, e);
            ack.acknowledge();

        } catch (RedisSystemException e) {

            if (isDuplicate(e)) {
                log.info("Duplicate comment ignored");
                ack.acknowledge();
            } else {
                throw e;
            }
        }
    }

    private boolean isDuplicate(RedisSystemException e) {
        return e.getMessage() != null &&
                e.getMessage().contains("already exists");
    }
}