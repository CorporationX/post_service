package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafkaevents.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommentEventPublisher extends AbstractEventPublisher implements KafkaEventPublisher<CommentEvent> {

    @Value("${spring.data.kafka.topic.comment}")
    private String topic;

    public CommentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    public void publish(CommentEvent event) {
        sendMessage(event, topic);
    }
}
