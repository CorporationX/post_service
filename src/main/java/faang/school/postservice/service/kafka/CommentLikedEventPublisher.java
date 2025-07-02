package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.notification.CommentLikedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentLikedEventPublisher extends EventPublisher<CommentLikedEvent> {

    public CommentLikedEventPublisher(KafkaTemplate<String, CommentLikedEvent> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
