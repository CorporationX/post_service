package faang.school.postservice.producer;

import faang.school.postservice.event.kafka.KafkaPostLikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostLikeEventProducer extends AbstractProducer<KafkaPostLikeEvent> {
    public KafkaPostLikeEventProducer(NewTopic likesTopic,
                                      KafkaTemplate<String, Object> kafkaTemplate) {
        super(likesTopic, kafkaTemplate);
    }
}
