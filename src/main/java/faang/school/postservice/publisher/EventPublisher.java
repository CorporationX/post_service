package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
public abstract class EventPublisher<T extends NotificationEvent> {

    private final String topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void publish(T event) {
        kafkaTemplate.send(topic, event);

        log.info("Published event to topic {}: {}", topic, event);
    }
}
