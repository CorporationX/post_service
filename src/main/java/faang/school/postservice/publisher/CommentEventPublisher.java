package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.comment-event}")
    private String analyticsTopic;

    public void publish(long postId,
                        long authorId,
                        long commentId,
                        LocalDateTime createdAt) {
        CommentEventDto commentEventDto = new CommentEventDto(postId,
                authorId,
                commentId,
                createdAt);
        try {
            kafkaTemplate.send(analyticsTopic, commentEventDto);
            log.debug("Successfully published comment event: {}", commentEventDto);
        } catch (Exception exception) {
            log.error("Failed to publish comment event: {}", commentEventDto, exception);
        }
    }
}
