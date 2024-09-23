package faang.school.postservice.messaging.publisher.kafka.like;

import faang.school.postservice.event.kafka.LikeKafkaEvent;
import faang.school.postservice.messaging.publisher.kafka.AbstractKafkaEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikePublisher extends AbstractKafkaEventPublisher<LikeKafkaEvent> {
    public KafkaLikePublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              NewTopic likesTopic) {
        super(kafkaTemplate, likesTopic);
    }

    @Override
    public void publish(LikeKafkaEvent event) {
        super.publish(event);
    }
}
