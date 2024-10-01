package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostViewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaPostViewProducer extends KafkaProducer<PostViewEvent> {
    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${spring.kafka.topic.post-views.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
