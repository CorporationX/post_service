package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.CreatePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, CreatePostEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.post-topic}")
    private String postsTopic;

    public void publishPostEvent(CreatePostEvent kafkaPostEvent) {
        kafkaTemplate.send(postsTopic, kafkaPostEvent);
    }
}
