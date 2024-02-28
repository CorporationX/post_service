package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.PostViewEvent;
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
public class AsyncPostViewEventPublisher {
    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.post_view.name}")
    private String postViewTopic;

    @Async("taskExecutor")
    public void asyncPublish(PostViewEvent postEvent) {
        CompletableFuture<SendResult<String, PostViewEvent>> future = kafkaTemplate.send(postViewTopic, postEvent);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Post view event was sent: {}", result);
            } else {
                log.error("Failed to send post view event", e);
            }
        });
    }
}
