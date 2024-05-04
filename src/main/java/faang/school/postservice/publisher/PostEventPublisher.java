package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractKafkaEventPublisher<PostEvent> {

    public PostEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper, NewTopic postEventTopic) {
        super(kafkaTemplate, objectMapper, postEventTopic);
    }
}
