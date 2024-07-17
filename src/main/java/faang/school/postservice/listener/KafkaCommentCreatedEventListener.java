package faang.school.postservice.listener;

import faang.school.postservice.event.CommentCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaCommentCreatedEventListener extends AbstractKafkaListener<CommentCreatedEvent> {

    @KafkaListener(topics = "${spring.data.kafka.comments-topic}", groupId = "comments")
    public void listen(String message) {
        consume(message, CommentCreatedEvent.class, this::handle);
    }

    @Override
    public void handle(CommentCreatedEvent event) {
        log.info("Received comment created event: {}", event);
    }
}
