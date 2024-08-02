package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostViewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractEventProducer<PostViewEvent>{
    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${spring.data.kafka.topics.post_views.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
