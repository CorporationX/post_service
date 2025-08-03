package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka.events.PostCommentedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentProducer extends AbstractKafkaEventProducer<PostCommentedEvent> {

    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    private static final String TOPIC = "comment.made";

    @Override
    protected String getTopic() {
        return TOPIC;
    }

}
