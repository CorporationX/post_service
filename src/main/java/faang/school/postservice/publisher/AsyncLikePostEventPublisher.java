package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.LikePostEvent;
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
public class AsyncLikePostEventPublisher {
    private final KafkaTemplate<String, LikePostEvent> kafkaTemplate;

    @Value("${spring.kafka.topics.like_post.name}")
    private String likePostTopic;

    @Async("taskExecutor")
    public void asyncPublish(LikePostEvent event) {
        CompletableFuture<SendResult<String, LikePostEvent>> future = kafkaTemplate.send(likePostTopic, event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Like post event was sent: {}", result);
            } else {
                log.error("Failed to send like post event", e);
            }
        });
    }
}
