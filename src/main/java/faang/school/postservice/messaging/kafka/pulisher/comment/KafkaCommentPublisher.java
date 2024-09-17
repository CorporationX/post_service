package faang.school.postservice.messaging.kafka.pulisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.messaging.kafka.pulisher.AbstractKafkaPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentPublisher extends AbstractKafkaPublisher<CommentEvent> {

    public KafkaCommentPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${spring.kafka.topic.comments}") String commentTopicName) {
        super(kafkaTemplate, objectMapper, commentTopicName);
    }

    @Override
    public void publish(CommentEvent event) {
        super.publish(event);
    }
}
