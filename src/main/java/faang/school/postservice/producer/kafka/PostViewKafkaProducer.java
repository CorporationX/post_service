package faang.school.postservice.producer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewKafkaProducer extends AbstractKafkaProducer<PostViewEvent> {
    public PostViewKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            NewTopic postViewTopic,
            ObjectMapper objectMapper
    ) {
        super(kafkaTemplate, postViewTopic, objectMapper);
    }
}
