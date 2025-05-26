package faang.school.postservice.kafka;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.KafkaConsumerException;
import faang.school.postservice.service.redis.RedisCommentService;
import faang.school.postservice.utils.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static faang.school.postservice.contants.ErrorMessage.FAILED_TO_DESERIALIZE_EVENT;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final RedisCommentService redisCommentService;
    private final JsonUtils jsonUtils;

    @KafkaListener(topics = "${spring.kafka.listener.topics.news-feed-comment}",
            groupId = "${spring.kafka.listener.groupId.news-feed-group}")
    public void listen(String message, Acknowledgment ack) {
        try {
            CommentEvent event = jsonUtils.fromJson(message, CommentEvent.class);
            redisCommentService.saveComment(event);
            log.info("Received message {}", event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", message, e);
            throw new KafkaConsumerException(String.format(FAILED_TO_DESERIALIZE_EVENT, CommentEvent.class.getSimpleName()));
        }
    }
}