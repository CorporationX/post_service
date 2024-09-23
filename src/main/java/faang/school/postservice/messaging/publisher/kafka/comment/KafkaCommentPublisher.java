package faang.school.postservice.messaging.publisher.kafka.comment;

import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.messaging.publisher.kafka.AbstractKafkaEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentPublisher extends AbstractKafkaEventPublisher<CommentKafkaEvent> {
    public KafkaCommentPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                 NewTopic commentsTopic) {
        super(kafkaTemplate, commentsTopic);
    }

    @Override
    public void publish(CommentKafkaEvent event) {
        super.publish(event);
    }
}
