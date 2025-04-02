package faang.school.postservice.producer;

import faang.school.postservice.producer.event.PostLikeKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends MessagePublisher<PostLikeKafkaEvent> {
    public KafkaLikeProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.likes.name}") String topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    public void publish(PostLikeKafkaEvent postLikeKafkaEvent) {
        super.publish(postLikeKafkaEvent);
    }
}
