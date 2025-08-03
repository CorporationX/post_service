package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka.events.PostCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostCreatedProducer extends AbstractKafkaEventProducer<PostCreatedEvent> {

    private static final String TOPIC = "post.created";

    public KafkaPostCreatedProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }
}
