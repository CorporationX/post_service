package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedLikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractEventProducer<FeedLikeEvent> {
    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.data.kafka.topics.like}") String topic) {
        super(kafkaTemplate, topic);
    }
}
