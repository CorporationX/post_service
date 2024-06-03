package faang.school.postservice.publisher.kafka.createcomment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.kafka.AbstractEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    public CommentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic commentEventTopic) {
        super(kafkaTemplate, objectMapper, commentEventTopic);
    }
}