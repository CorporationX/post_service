package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.CommentEventDto;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class KafkaCommentConsumer {

    private final StringRedisTemplate redisTemplate;

    @Value("${post.comments.max-size}")
    private int maxComments;

    @KafkaListener(topics = "comments",
            groupId = "comments-group"
    )
    public void listen(CommentEventDto event, Acknowledgment ack) {

        String key = "post:" + event.postId() + ":comments";

        try {
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

            redisTemplate.opsForStream().trim(key, maxComments);

            ack.acknowledge();

        } catch (RedisSystemException e) {
            if (e.getMessage().contains("BUSYGROUP") || e.getMessage().contains("already exists")) {
                ack.acknowledge();
            } else {
                throw e;
            }
            ack.acknowledge();
        }
    }
}
