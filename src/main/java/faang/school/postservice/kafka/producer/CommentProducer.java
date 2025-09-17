package faang.school.postservice.kafka.producer;

import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommentProducer extends AbstractKafkaProducer<CommentEvent> {

    private final KafkaTopicsProperties topics;

    public CommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           KafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publishCommentEvent(CommentEvent event) {
        publishEvent(topics.comment(), String.valueOf(event.postId()), event);
    }
}
