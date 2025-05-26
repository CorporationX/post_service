package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.comment.CommentCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentCreateEvent> {

    @Value("${spring.kafka.topic.comment-topic}")
    private String commentTopic;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publishCommentEvent(CommentCreateEvent commentEvent) {
        publishEvent(commentTopic, commentEvent);
    }
}
