package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentProducer extends AbstractEventProducer<CommentEvent> {
    public CommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           @Value("${spring.kafka.topics-name.comments}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}