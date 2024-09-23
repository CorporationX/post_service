package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractEventProducer<LikeEvent>{
    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topic-name.likes}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
