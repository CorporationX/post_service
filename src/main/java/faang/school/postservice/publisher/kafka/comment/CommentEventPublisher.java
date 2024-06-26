package faang.school.postservice.publisher.kafka.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.kafka.AbstractEventKafkaPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventKafkaPublisher<CommentEvent> {

    public CommentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic commentEventTopic) {
        super(kafkaTemplate, objectMapper, commentEventTopic);
    }
}