package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostViewKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractEventProducer<PostViewKafkaEvent> {

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic postViews) {
        super(kafkaTemplate, postViews);
    }

    @Override
    public void sendEvent(PostViewKafkaEvent event) {
        super.sendEvent(event);
    }
}
