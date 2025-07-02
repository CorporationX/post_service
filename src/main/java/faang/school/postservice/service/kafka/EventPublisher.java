package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class EventPublisher<T extends NotificationEvent> {

    private final NewTopic topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void publish(T event) {
        kafkaTemplate.send(topic.name(), event);

        log.info("Published event to topic {}: {}", topic.name(), event);
    }
}
