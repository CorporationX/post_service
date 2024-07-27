package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.post.PostViewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewProducer extends AbstractEventProducer<PostViewEvent> {
    public PostViewProducer(KafkaTemplate<String, Object> kafkaTemplate,
                            @Value("${spring.kafka.topics-name.post-views}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}