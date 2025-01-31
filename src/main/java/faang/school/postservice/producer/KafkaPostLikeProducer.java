package faang.school.postservice.producer;

import faang.school.postservice.event.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostLikeProducer implements KafkaPublisher<PostLikeEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postLikeKafkaTopic;

    @Override
    public void publish(PostLikeEvent event) {
        kafkaTemplate.send(postLikeKafkaTopic.name(), event);
        log.info("Like event was sent to Kafka topic {}: {} ", postLikeKafkaTopic.name(), event);
    }
}
