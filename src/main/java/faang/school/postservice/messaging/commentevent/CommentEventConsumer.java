package faang.school.postservice.messaging.commentevent;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventConsumer {
    private final KafkaTemplate<String, CommentDto> kafkaTemplate;

    @Value("${spring.kafka.topics.comment-publication}")
    private String commentPublicationTopic;

    public void publish(CommentDto commentDto) {
       CompletableFuture<SendResult<String, CommentDto>> future = kafkaTemplate.send(commentPublicationTopic, commentDto);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Event was sent: {}", result);
            } else {
                log.error("Failed to send event: {}", e.getMessage());
            }
        });
    }
}
