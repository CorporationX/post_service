package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.CommentEventDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaCommentConsumer {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${post.comments.max-size}")
    private int maxComments;

    @KafkaListener(topics = "comments",
                   groupId = "comments-group")
    public void listen(CommentEventDto event, Acknowledgment ack) {



    }

    private String serialize(CommentEventDto event) {
        try {
            return new ObjectMapper().writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
