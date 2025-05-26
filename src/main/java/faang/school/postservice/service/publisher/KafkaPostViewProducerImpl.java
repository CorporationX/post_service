package faang.school.postservice.service.publisher;

import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewProducerImpl implements KafkaPostViewProducer {

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @Value("${kafka.topic.post-viewed-event.name}")
    private String topic;

    @Override
    public void publishPostViewEvent(Long postId, Long userId) {
        PostViewEvent event = PostViewEvent.builder()
                .postId(postId)
                .userId(userId)
                .viewedAt(Instant.now())
                .build();
        try {
            kafkaTemplate.send(topic, event);
            log.info("Event: {}, sent to topic: {}", event, topic);
        } catch (Exception e) {
            log.error("Failed to send event to topic: {}", topic, e);
        }
    }
}
