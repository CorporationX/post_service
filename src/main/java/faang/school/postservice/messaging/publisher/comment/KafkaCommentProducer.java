package faang.school.postservice.messaging.publisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.messaging.publisher.AbstractKafkaEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaEventPublisher<CommentEvent> {

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Value("${spring.kafka.topics.comment}") String topicName) {
        super(kafkaTemplate, objectMapper, topicName);
    }

    @Override
    public void publish(CommentEvent event) {
        super.publish(event);
    }
}