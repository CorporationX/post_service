package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.event.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeProducer extends AbstractEventProducer<LikeEvent> {
    public LikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${spring.kafka.topics-name.likes}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}