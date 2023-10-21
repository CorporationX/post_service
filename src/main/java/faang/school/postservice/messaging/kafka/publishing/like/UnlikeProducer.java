package faang.school.postservice.messaging.kafka.publishing.like;

import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.messaging.kafka.publishing.AbstractProducer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UnlikeProducer extends AbstractProducer<LikeEvent> {
    @Setter
    @Value("${spring.kafka.channels.unlike_event_channel.name}")
    private String topic;

    @Autowired
    public UnlikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(LikeEvent event) {
        kafkaTemplate.send(topic, event)
                .thenAccept(ack -> log.info("UnlikeEvent (id: {}; postId: {}) has been delivered (Kafka)",
                        event.getId(), event.getPostId()))
                .exceptionally(ex -> {
                    log.error("Failed to publish UnlikeEvent (id: {}; postId: {}) (Kafka). Message: {}",
                            event.getId(), event.getPostId(), ex.getMessage());
                    return null;
                });
    }
}
