package faang.school.postservice.publisher.post;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements MessagePublisher<PostPublishedEvent> {
    public static final String TOPIC = "post_published";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
