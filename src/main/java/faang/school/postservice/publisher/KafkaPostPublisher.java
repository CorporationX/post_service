package faang.school.postservice.publisher;

import faang.school.postservice.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostPublisher implements MessagePublisher<PostCreatedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.data.kafka.posts-topic}")
    private String postsTopic;

    @Override
    public void publish(PostCreatedEvent event) {
        kafkaTemplate.send(postsTopic, event.toString());
    }
}
