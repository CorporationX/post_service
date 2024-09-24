package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractKafkaProducer<PostViewEvent> {
    public KafkaPostViewProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic postViewsTopic) {
        super(kafkaTemplate, objectMapper, postViewsTopic);
    }
}
