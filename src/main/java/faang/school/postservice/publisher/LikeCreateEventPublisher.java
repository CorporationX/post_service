package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.LikeCreateNotificationEvent;
import faang.school.postservice.dto.notification.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeCreateEventPublisher extends AbstractEventPublisher<LikeCreateNotificationEvent> {

    public LikeCreateEventPublisher(@Value("${spring.kafka.topics.like_created-topic.name}") String topic,
                                    KafkaTemplate<String, LikeCreateNotificationEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
