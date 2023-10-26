package faang.school.postservice.messaging.kafka.listener;

import faang.school.postservice.messaging.kafka.events.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@Component
public class CommentConsumer {

    @KafkaListener(topics = "${spring.kafka.channels.comment_event_channel.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(CommentEvent message, Acknowledgment ack) {
        log.info("Event (id: {}; postId: {}) has been delivered (Kafka)", message.getId(), message.getPostId());
        ack.acknowledge();
    }
}
