package faang.school.postservice.producer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostKafkaProducer extends AbstractKafkaProducer<PostEvent> {
    public PostKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            NewTopic postTopic,
            ObjectMapper objectMapper
    ) {
        super(kafkaTemplate, postTopic, objectMapper);
    }
}
