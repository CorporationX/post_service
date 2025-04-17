package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostCreatedEvent> {
    public PostEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                             ObjectMapper objectMapper,
                             @Value(value = "${spring.data.kafka.topic.post.name}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }

    @Override
    public void sendEvent(PostCreatedEvent event) {
        super.sendEvent(event);
    }
}
