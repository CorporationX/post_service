package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class CommentProducer extends MessagePublisher<CommentEvent> {
    public CommentProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topics.comment.name}") String topic
    ) {
        super(kafkaTemplate, topic);
    }

    @Override
    @Retryable(
            backoff = @Backoff(delay = 2000)
    )
    public void publish(CommentEvent message) {
        kafkaTemplate.send(topic, message).join();
    }
}
