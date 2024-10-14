package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.kafka.KafkaPostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewEventProducer extends AbstractProducer<KafkaPostViewEvent> {
    public KafkaPostViewEventProducer(NewTopic postsViewTopic,
                                      KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper) {
        super(postsViewTopic, kafkaTemplate, objectMapper);
    }
}
