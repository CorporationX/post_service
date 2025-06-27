package faang.school.postservice.producers;

import faang.school.postservice.dto.comment.CommentSendEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final KafkaTemplate<String, CommentSendEvent> kafkaTemplate;

    @Value("${spring.data.kafka.topic.comment.name}")
    private String commentTopic;

    public void sendCommentCreatedEvent(CommentSendEvent event) {
        kafkaTemplate.send(commentTopic, event);
    }
}
