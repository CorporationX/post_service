package faang.school.postservice.service.producer;

import faang.school.postservice.dto.event.CommentEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

public class CommentProducer extends EventProducer<CommentEvent> {

    public CommentProducer(KafkaTemplate<String, Object> template, NewTopic topic) {
        super(template, topic);
    }

    public void sendPostViewEvent(CommentEvent event) {
        super.sendEvent(event);
    }
}
