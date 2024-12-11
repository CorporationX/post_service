package faang.school.postservice.kafka.producer;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeKafkaProducer extends AbstractKafkaProducer<LikeKafkaEvent> {

    @Value("${kafka.topics.like}")
    private String likeTopic;

    public LikeKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return likeTopic;
    }
}
