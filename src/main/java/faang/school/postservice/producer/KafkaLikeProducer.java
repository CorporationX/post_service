package faang.school.postservice.producer;

import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractEventProducer<LikeEvent>{
    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.data.kafka.topics.likes.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
