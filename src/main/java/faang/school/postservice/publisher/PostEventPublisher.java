package faang.school.postservice.publisher;

import faang.school.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {

    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Value("${kafka.topic.post}")
    private String postTopic;

    public void publish(PostEvent postEvent) {
        CompletableFuture<SendResult<String, PostEvent>> future = kafkaTemplate.send(postTopic, postEvent);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send post event for post id {}", postEvent.postId());
            } else {
                log.info("Sent post event for post id {}", postEvent.postId());
            }
        });
    }
}