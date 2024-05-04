package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractKafkaEventPublisher<CommentEvent> {

    public CommentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper, NewTopic commentEventTopic) {
        super(kafkaTemplate, objectMapper, commentEventTopic);
    }
}