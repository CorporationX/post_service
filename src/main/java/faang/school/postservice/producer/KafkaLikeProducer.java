package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.LikeCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaLikeProducer extends KafkaProducer<LikeCreatedEvent> {
    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topic.like.name}")String topic) {
        super(kafkaTemplate, topic);
    }
}
