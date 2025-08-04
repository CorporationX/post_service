package faang.school.postservice.service.like.kafka;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLikePublisher {

    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;

    @Async("kafkaExecutor")
    public void publishLikeEvent(LikeEvent likeEvent) {
        log.info("Publishing like event: {}", likeEvent);
        kafkaTemplate.send("likes", likeEvent).completeExceptionally(
                new RuntimeException("Failed to send like event to Kafka: " + likeEvent));
    }

}
