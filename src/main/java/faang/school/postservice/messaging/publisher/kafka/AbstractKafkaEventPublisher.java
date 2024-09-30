package faang.school.postservice.messaging.publisher.kafka;

import faang.school.postservice.messaging.EventPublisher;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class AbstractKafkaEventPublisher<T> implements EventPublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private NewTopic newTopic;
    @Override
    public void publish(T event) {
        kafkaTemplate.send(newTopic.name(), event);
        log.info("Event: {} was sent to kafka topic: {}", event, newTopic.name());
    }
}