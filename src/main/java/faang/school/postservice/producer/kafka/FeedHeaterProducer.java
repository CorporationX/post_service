package faang.school.postservice.producer.kafka;

import faang.school.postservice.dto.event.FeedHeaterEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedHeaterProducer extends AbstractEventProducer<FeedHeaterEvent> {
    public FeedHeaterProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              @Value("${spring.kafka.topics-name.feed-heater}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
