package faang.school.postservice.producer;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractEventProducer<LikeKafkaEvent> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic likes) {
        super(kafkaTemplate, likes);
    }

    @Override
    public void sendEvent(LikeKafkaEvent event) {
        super.sendEvent(event);
    }
}
