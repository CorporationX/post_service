package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractEventProducer<PostViewEvent> {

    @Autowired
    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate, @Qualifier("postViewsTopic") NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void sendEvent(PostViewEvent event) {
        super.sendEvent(event);
    }
}
