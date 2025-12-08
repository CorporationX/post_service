package faang.school.postservice.producer;

import faang.school.postservice.dto.event.CommentEvent;
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
public class CommentEventPublisherImpl implements CommentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.comment-events.name}")
    private String topic;

    @Override
    public void publish(CommentEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, String.valueOf(event.getPostId()), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Comment event sent successfully: commentId={}, postId={}, partition={}, offset{}",
                        event.getCommentId(),
                        event.getPostId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send comment event: commentId={}, postId={}, error={}",
                        event.getCommentId(),
                        event.getPostId(),
                        ex.getMessage());
            }
        });
    }

}
