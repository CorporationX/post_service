package faang.school.postservice.producer.kafka;

import faang.school.postservice.event.comment.CommentKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentKafkaEvent>{

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic comments) {
        super(kafkaTemplate, comments);
    }

    @Override
    public void sendEvent(CommentKafkaEvent event) {
        super.sendEvent(event);
    }
}
