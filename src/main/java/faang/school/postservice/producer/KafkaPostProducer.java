package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.KafkaPostEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends KafkaProducer<KafkaPostEvent> {
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topic.posts.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
