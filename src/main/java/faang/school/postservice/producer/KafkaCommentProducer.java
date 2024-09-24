package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentEvent> {
    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic commentsTopic) {
        super(kafkaTemplate, objectMapper, commentsTopic);
    }
}
