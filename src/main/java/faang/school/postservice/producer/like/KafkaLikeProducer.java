package faang.school.postservice.producer.like;

import faang.school.postservice.event.like.LikePostEvent;
import faang.school.postservice.producer.AbstractKafkaMessageProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractKafkaMessageProducer<LikePostEvent> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic likeTopic) {
        super(kafkaTemplate, likeTopic);
    }
}
