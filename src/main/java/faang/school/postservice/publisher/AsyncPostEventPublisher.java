package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEvent;
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
public class AsyncPostEventPublisher {
    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    @Async("taskExecutor")
    public void asyncPublishBatchEvent(PostEvent event) {
        CompletableFuture<SendResult<String, PostEvent>> future = kafkaTemplate.send(postTopic, event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Post event was sent: {}", result);
            } else {
                log.error("Failed to send post event", e);
            }
        });
    }
}
