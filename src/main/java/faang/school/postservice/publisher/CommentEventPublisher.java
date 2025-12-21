package faang.school.postservice.publisher;

import faang.school.postservice.config.kafka.CommentTopicProperties;
import faang.school.postservice.dto.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentTopicProperties commentTopicProperties;

    public void publish(long postId,
                        long authorId,
                        long commentId,
                        String content,
                        LocalDateTime createdAt) {
        CommentEventDto commentEventDto = new CommentEventDto(postId,
                authorId,
                commentId,
                content,
                createdAt);
        log.info("Publishing comment event: post Id = {}, author Id = {}, comment Id = {}, content = {}",
                postId,
                authorId,
                commentId,
                content);
        List<String> topics = commentTopicProperties.getCommentEvents();
        if (topics == null || topics.isEmpty()) {
            log.warn("No Kafka topics configured for comment events");
            return;
        }
        topics.forEach(topic -> send(topic, commentEventDto));
    }

    private void send(String topic, CommentEventDto event) {
        try {
            kafkaTemplate.send(topic, event);
        } catch (Exception e) {
            log.error("Failed to send event to topic {}", topic, e);
        }
    }
}