package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class MessagePublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public void publish(T event) {
        kafkaTemplate.send(topic, event);
    }

    public void publishBatch(Collection<T> events) {
        events.forEach(event -> kafkaTemplate.send(topic, event));
    }
}
