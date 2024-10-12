package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostEventProducer extends AbstractProducer<PostEvent> {
    public KafkaPostEventProducer(NewTopic postsTopic,
                                  KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        super(postsTopic, kafkaTemplate, objectMapper);
    }
}
