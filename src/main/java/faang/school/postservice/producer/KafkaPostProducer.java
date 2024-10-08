package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostCreatedEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends KafkaProducer<PostCreatedEvent> {
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topic.posts.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
