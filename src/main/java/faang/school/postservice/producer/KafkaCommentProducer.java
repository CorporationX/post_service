package faang.school.postservice.producer;

import faang.school.postservice.dto.event.CommentKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractEventProducer<CommentKafkaEvent> {

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic comments) {
        super(kafkaTemplate, comments);
    }

    @Override
    public void sendEvent(CommentKafkaEvent event) {
        super.sendEvent(event);
    }
}
