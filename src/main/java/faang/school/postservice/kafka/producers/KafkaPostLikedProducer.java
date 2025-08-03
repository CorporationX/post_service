package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka.events.PostLikedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostLikedProducer extends AbstractKafkaEventProducer<PostLikedEvent> {

    private static final String TOPIC = "post.liked";

    public KafkaPostLikedProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }
}
