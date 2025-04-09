package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostCreatedEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }
}
