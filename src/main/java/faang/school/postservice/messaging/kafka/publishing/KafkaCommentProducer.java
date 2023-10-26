package faang.school.postservice.messaging.kafka.publishing;

import faang.school.postservice.messaging.kafka.events.CommentEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaCommentProducer extends AbstractProducer<CommentEvent> {

    @Setter
    @Value("${spring.kafka.channels.comment_event_channel.name}")
    private String commentEventChannel;

    @Autowired
    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(CommentEvent event) {
        kafkaTemplate.send(commentEventChannel, event)
                .thenAccept(ack -> log.info("CommentEvent (id: {}; postId: {}) has been delivered (Kafka)",
                        event.getId(), event.getPostId()))
                .exceptionally(ex -> {
                    log.error("Failed to publish CommentEvent (id: {}; postId: {}) (Kafka). Message: {}",
                            event.getId(), event.getPostId(), ex.getMessage());
                    return null;
                });
    }
}
