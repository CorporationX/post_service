package faang.school.postservice.producer;

import faang.school.postservice.event.PostCreatedEvent;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final GenericKafkaProducer<PostCreatedEvent> producer;
    private final KafkaProperties kafkaProperties;

    public void sendPostCreatedEvent(PostCreatedEvent event) {
        String topic = kafkaProperties.getTopics().getPostCreated();
        producer.sendEvent(topic, event.getPostId().toString(), event, "PostCreatedEvent");
    }
}
