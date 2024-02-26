package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncCommentEventPublisher {
    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;

    @Async("taskExecutor")
    public void asyncPublish(CommentEvent event) {
        CompletableFuture<SendResult<String, CommentEvent>> future = kafkaTemplate.send(commentTopic, event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Comment event was sent: {}", result);
            } else {
                log.error("Failed to send comment event", e);
            }
        });
    }
}
