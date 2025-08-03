package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka.events.PostViewedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostViewedProducer extends AbstractKafkaEventProducer<PostViewedEvent> {

    private static final String TOPIC = "post.viewed";

    public KafkaPostViewedProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }
}
