package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractKafkaProducer<PostEvent> {
    public KafkaPostProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic postTopic) {
        super(kafkaTemplate, objectMapper, postTopic);
    }
}
