package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.post.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostProducer extends AbstractEventProducer<PostEvent> {
    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${spring.kafka.topics-name.posts}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}