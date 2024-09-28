package faang.school.postservice.producer;

import faang.school.postservice.dto.publishable.FeedHeaterEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaFeedHeaterProducer extends AbstractEventProducer<FeedHeaterEvent>{
    public KafkaFeedHeaterProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                   @Value("${spring.kafka.topic-name.feed-heater}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}
