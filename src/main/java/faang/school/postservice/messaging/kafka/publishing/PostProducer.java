package faang.school.postservice.messaging.kafka.publishing;

import faang.school.postservice.messaging.kafka.events.PostEvent;
import faang.school.postservice.messaging.publishing.Publishable;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostProducer extends AbstractProducer<PostEvent> {

    @Setter
    @Value("${spring.kafka.channels.post_event_channel.name}")
    private String topic;

    @Autowired
    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(PostEvent event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(ack -> log.info("Post event has published to kafka {}, {}", topic, event))
                .exceptionally(e -> {
                    log.error("Failed to publish on topic {}, event {}", topic, event);
                    return null;
                });
    }
}
