package faang.school.postservice.producer.comment;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.comment.CommentCreatedEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentCreatedEvent> {

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate, KafkaProperties kafkaProperties) {
        super(kafkaTemplate, kafkaProperties.getTopics().getCommentCreatedTopic().getName(), kafkaProperties);
    }
}
