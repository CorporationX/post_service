package faang.school.postservice.service.producer;

import faang.school.postservice.dto.event.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

public class PostViewProducer extends EventProducer<PostViewEvent> {

    public PostViewProducer(KafkaTemplate<String, Object> template, NewTopic topic) {
        super(template, topic);
    }

    public void sendPostViewEvent(PostViewEvent event) {
        super.sendEvent(event);
    }
}
