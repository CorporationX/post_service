package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostEvent>{
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topic-name.posts}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
