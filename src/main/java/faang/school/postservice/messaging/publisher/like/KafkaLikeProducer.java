package faang.school.postservice.messaging.publisher.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.messaging.publisher.AbstractKafkaEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractKafkaEventPublisher<LikeEvent> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             ObjectMapper objectMapper,
                             @Value("${spring.kafka.topics.like}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }

    @Override
    public void publish(LikeEvent event) {
        super.publish(event);
    }
}
