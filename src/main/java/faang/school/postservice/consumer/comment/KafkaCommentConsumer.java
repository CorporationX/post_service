package faang.school.postservice.consumer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.service.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.comment}", groupId = "comments-group")
    public void listen(String message, Acknowledgment ack) {
        try {
            CommentEventDto event = objectMapper.readValue(message, CommentEventDto.class);
            redisService.savePostForComments(event);

            ack.acknowledge();

        } catch (JsonProcessingException e) {
            log.error("Invalid JSON message: {}", message, e);
            ack.acknowledge();
        }
    }
}