package faang.school.postservice.producer;

import faang.school.postservice.dto.event.LikeEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends KafkaAbstractProducer<LikeEventDto> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic likes) {
        super(kafkaTemplate, likes);
    }
}
