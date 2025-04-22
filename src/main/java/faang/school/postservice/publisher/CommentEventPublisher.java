package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaTopic;
import faang.school.postservice.dto.kafkaevents.CommentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommentEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<CommentEvent> {

    private final KafkaTopic kafkaTopic;

    public CommentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, KafkaTopic kafkaTopic) {
        super(kafkaTemplate, objectMapper);
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void publish(CommentEvent event) {
        sendMessage(event, kafkaTopic.comment().name());
    }
}
