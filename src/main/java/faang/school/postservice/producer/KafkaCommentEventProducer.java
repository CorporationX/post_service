package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.kafka.KafkaCommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentEventProducer extends AbstractProducer<KafkaCommentEvent> {
    public KafkaCommentEventProducer(NewTopic commentsTopic,
                                     KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper) {
        super(commentsTopic, kafkaTemplate, objectMapper);
    }
}
