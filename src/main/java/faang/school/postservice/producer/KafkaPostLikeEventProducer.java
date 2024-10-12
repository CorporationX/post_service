package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostLikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostLikeEventProducer extends AbstractProducer<PostLikeEvent> {
    public KafkaPostLikeEventProducer(NewTopic likesTopic,
                                      KafkaTemplate<String, String> kafkaTemplate,
                                      ObjectMapper objectMapper) {
        super(likesTopic, kafkaTemplate, objectMapper);
    }
}
