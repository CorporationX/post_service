package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.PostViewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractEventProducer<PostViewEvent>{
    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${spring.kafka.topic-name.post-views}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
