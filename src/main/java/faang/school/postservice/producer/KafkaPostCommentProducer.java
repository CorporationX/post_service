package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostCommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostCommentProducer extends AbstractKafkaProducer<KafkaPostCommentEvent>{
    public KafkaPostCommentProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    NewTopic postCommentTopic, ObjectMapper objectMapper) {
        super(kafkaTemplate, postCommentTopic, objectMapper);
    }
}
