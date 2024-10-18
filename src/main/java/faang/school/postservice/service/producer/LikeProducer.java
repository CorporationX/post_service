package faang.school.postservice.service.producer;

import faang.school.postservice.dto.event.LikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

public class LikeProducer extends EventProducer<LikeEvent> {

    public LikeProducer(KafkaTemplate<String, Object> template, NewTopic topic) {
        super(template, topic);
    }

    @Override
    public void sendEvent(LikeEvent event) {
        super.sendEvent(event);
    }
}
