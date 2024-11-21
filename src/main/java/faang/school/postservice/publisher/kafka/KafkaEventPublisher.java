package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEvent(T event, String topic) {
        kafkaTemplate.send(topic, event)
                .thenRunAsync(() -> log.info("Event published: {}\nto topic: {}", event, topic));
    }
}
