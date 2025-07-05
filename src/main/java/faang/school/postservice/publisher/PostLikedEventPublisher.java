package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.PostLikedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostLikedEventPublisher extends EventPublisher<PostLikedEvent> {

    public PostLikedEventPublisher(
            @Value(value = "${spring.kafka.topics.like.post-like-topic.name}") String topic,
            KafkaTemplate<String, PostLikedEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
