package faang.school.postservice.producer;

import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentsProducer extends AbstractEventProducer<CommentEvent>{
    public KafkaCommentsProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${spring.data.kafka.topics.comments.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
