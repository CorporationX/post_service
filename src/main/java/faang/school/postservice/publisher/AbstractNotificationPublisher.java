package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractNotificationPublisher<T extends NotificationEvent> extends AbstractEventPublisher<T>{
    public AbstractNotificationPublisher(String topic, KafkaTemplate<String, T> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
