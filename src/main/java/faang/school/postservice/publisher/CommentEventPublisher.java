package faang.school.postservice.publisher;

import faang.school.postservice.event.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    @Value("${spring.data.kafka.channels.comment-channel.name}")
    private String commentEventChannel;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(CommentEvent commentEvent) {
        kafkaTemplate.send(commentEventChannel, commentEvent);
        log.info("Comment event published: {}", commentEvent);
    }
}