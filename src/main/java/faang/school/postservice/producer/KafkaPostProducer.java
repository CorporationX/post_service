package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostKafkaEvent>{

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic posts) {
        super(kafkaTemplate, posts);
    }

    @Override
    public void sendEvent(PostKafkaEvent event) {
        super.sendEvent(event);
    }
}