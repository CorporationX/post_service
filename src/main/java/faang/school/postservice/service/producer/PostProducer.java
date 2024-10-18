package faang.school.postservice.service.producer;

import faang.school.postservice.dto.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

public class PostProducer extends EventProducer<PostEvent> {

    public PostProducer(KafkaTemplate<String, Object> template, NewTopic topic) {
        super(template, topic);
    }

    @Override
    public void sendEvent(PostEvent event) {
        super.sendEvent(event);
    }
}
