package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostFeedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostFeedEvent> {
    public PostEventProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic postTopic) {
        super(kafkaTemplate, postTopic);
    }
}