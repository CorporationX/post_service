package faang.school.postservice.messaging.kafka.publishing;

import faang.school.postservice.messaging.kafka.events.PostViewEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostViewProducer extends AbstractProducer<PostViewEvent> {

    @Setter
    @Value("${spring.kafka.channels.post_view_event_channel.name}")
    private String topic;

    @Autowired
    public PostViewProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(PostViewEvent event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(ack -> {
                    log.info("Post view event has published to kafka {}, {}", topic, event);
                    retryCount.set(0);
                })
                .exceptionally(e -> {
                    log.error("Failed to publish on topic {}, event {}", topic, event);
                    retryPublish(event);
                    return null;
                });
    }
}
