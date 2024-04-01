package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostLikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostLikeProducer extends AbstractKafkaProducer<KafkaPostLikeEvent> {

    public KafkaPostLikeProducer(KafkaTemplate<String, String> kafkaTemplate,
                                 NewTopic postLikeTopic, ObjectMapper objectMapper) {
        super(kafkaTemplate, postLikeTopic, objectMapper);
    }
}
