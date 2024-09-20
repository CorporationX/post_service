package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentEvent>{
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${spring.kafka.topic-name.comments}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
