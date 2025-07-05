package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.CommentLikedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentLikedEventPublisher extends EventPublisher<CommentLikedEvent> {

    public CommentLikedEventPublisher(
            @Value(value = "${spring.kafka.topics.like.comment-like-topic.name}") String topic,
            KafkaTemplate<String, CommentLikedEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
