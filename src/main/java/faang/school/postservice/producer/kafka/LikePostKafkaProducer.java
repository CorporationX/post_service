package faang.school.postservice.producer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikePostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikePostKafkaProducer extends AbstractKafkaProducer<LikePostEvent> {
    public LikePostKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            NewTopic likePostTopic,
            ObjectMapper objectMapper
    ) {
        super(kafkaTemplate, likePostTopic, objectMapper);
    }
}
