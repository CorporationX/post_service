package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.PostViewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaPostViewProducer extends AbstractEventProducer<PostViewEvent>{
    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${spring.kafka.topic-name.post_views}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
