package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private static final String COMMENT_TOPIC = "create_comment_topic";

    private final KafkaTemplate<Long, CommentEvent> commentEventKafkaTemplate;

    public void publish(CommentEvent event) {
        commentEventKafkaTemplate.send(COMMENT_TOPIC, event.getAuthorId(), event);
        log.info("✅ Publishing CommentEvent: {}", event);
    }
}