package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostDeleteEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostDeleteProducer extends AbstractEventProducer<FeedPostDeleteEvent> {
    public KafkaPostDeleteProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                   @Value("${spring.data.kafka.topics.post-delete.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
