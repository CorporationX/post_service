package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractKafkaProducer<LikeEvent> {
    public KafkaLikeProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, NewTopic likesTopic) {
        super(kafkaTemplate, objectMapper, likesTopic);
    }
}

