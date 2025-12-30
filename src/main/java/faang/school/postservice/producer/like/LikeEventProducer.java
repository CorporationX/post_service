package faang.school.postservice.producer.like;

import faang.school.postservice.dto.event.LikeAddedEvent;
import faang.school.postservice.exception.KafkaProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventProducer {

    @Value("${app.kafka.topics.like-added.name}")
    private String likeAddedTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLikeAddedEvent(LikeAddedEvent event) {
        kafkaTemplate.send(likeAddedTopicName, event)
                .thenRun(() -> log.info("Like added event is sent: {}", event))
                .exceptionally(ex -> {
                    log.error("Error occured. Event was not sent: {}", event);
                    throw new KafkaProcessingException(ex.getMessage());
                });
    }
}
