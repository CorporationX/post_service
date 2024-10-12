package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<FeedPostEvent> {
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.data.kafka.topics.post}") String topic) {
        super(kafkaTemplate, topic);
    }
}
