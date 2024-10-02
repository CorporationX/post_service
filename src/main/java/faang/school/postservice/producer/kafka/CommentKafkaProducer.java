package faang.school.postservice.producer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentKafkaProducer extends AbstractKafkaProducer<CommentEvent> {
    public CommentKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            NewTopic commentTopic,
            ObjectMapper objectMapper
    ) {
        super(kafkaTemplate, commentTopic, objectMapper);
    }
}
