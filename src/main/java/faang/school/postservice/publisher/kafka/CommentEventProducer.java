package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.CommentCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventProducer extends AbstractEventProducer<CommentCreatedEvent> {

    public CommentEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value(value = "${spring.data.kafka.topic.comments.name}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }
}
