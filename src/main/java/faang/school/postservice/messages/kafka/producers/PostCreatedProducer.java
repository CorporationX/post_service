package faang.school.postservice.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostCreatedProducer extends AbstractPublishKafka {
    public PostCreatedProducer(
            @Qualifier("kafkaTemplate")
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topics.post-created}")
            String postEvent
    ) {
        super(kafkaTemplate, postEvent);
    }
}