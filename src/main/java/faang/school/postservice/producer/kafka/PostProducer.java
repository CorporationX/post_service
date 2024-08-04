package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.post.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostProducer extends AbstractEventProducer<PostEvent> {
    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        NewTopic topic) {
        super(kafkaTemplate, topic);
    }
}