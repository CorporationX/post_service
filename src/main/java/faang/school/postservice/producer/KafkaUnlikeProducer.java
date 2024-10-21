package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedUnlikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUnlikeProducer extends AbstractEventProducer<FeedUnlikeEvent> {
    public KafkaUnlikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                               @Value("${spring.data.kafka.topics.unlike.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
