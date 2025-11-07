package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ANALYTICS_TOPIC = "analytics";

    public void publishComment(long postId, long actorId, long commentId, long postAuthorId, LocalDateTime createdAt) {
        CommentEventDto event = new CommentEventDto(postId, actorId, commentId, postAuthorId, createdAt);
        log.info("Publishing comment event: postId={}, actorId={}, commentId={}, postAuthorId={}",  postId, actorId, commentId, postAuthorId);
        try {
            kafkaTemplate.send(ANALYTICS_TOPIC, event);
            log.debug("Successfully published comment event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish comment event: {}", event, e);
        }
    }
}
