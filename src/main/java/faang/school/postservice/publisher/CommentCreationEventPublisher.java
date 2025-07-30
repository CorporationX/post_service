package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.CommentCreationNotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentCreationEventPublisher extends AbstractEventPublisher<CommentCreationNotificationEvent>{
    public CommentCreationEventPublisher(
            @Value("${spring.kafka.topics.comment-created-topic.name}") String topic,
            KafkaTemplate<String, CommentCreationNotificationEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
