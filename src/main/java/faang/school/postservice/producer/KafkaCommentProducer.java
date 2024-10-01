package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.CommentCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer  extends KafkaProducer<CommentCreatedEvent>{
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${spring.kafka.topic.comments.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
