package faang.school.postservice.messages.kafka.producers;

import faang.school.postservice.dto.post.PostCreatedEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostCreatedProducer extends AbstractPublishKafka<PostCreatedEvent> {
    public PostCreatedProducer(
            @Qualifier("postCreatedKafkaTemplate")
            KafkaTemplate<String, PostCreatedEvent> kafkaTemplate,
            @Value("${spring.kafka.topics.post-created}")
            String postEvent
    ) {
        super(kafkaTemplate, postEvent);
    }
}