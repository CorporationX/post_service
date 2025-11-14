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
public class CommentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ANALYTICS_TOPIC = "comment-events";

    public void publish(long postId,
                        long authorId,
                        long commentId,
                        LocalDateTime createdAt) {
        CommentEventDto commentEventDto = new CommentEventDto(postId,
                authorId,
                commentId,
                createdAt);
        log.info("Publishing comment event: post Id = {}, author Id = {}, comment Id = {}",
                postId,
                authorId,
                commentId);
        try {
            kafkaTemplate.send(ANALYTICS_TOPIC, commentEventDto);
            log.info("Successfully published comment event: {}", commentEventDto);
        } catch (Exception exception) {
            log.error("Failed to publish comment event: {}", commentEventDto, exception);
        }
    }
}
