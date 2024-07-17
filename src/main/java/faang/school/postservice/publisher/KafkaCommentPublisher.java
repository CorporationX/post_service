package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentPublisher implements MessagePublisher<CommentCreatedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.data.kafka.comments-topic}")
    private String commentTopic;

    @Override
    public void publish(CommentCreatedEvent event) {
        kafkaTemplate.send(commentTopic, event.toString());
    }
}
