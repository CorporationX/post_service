package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.kafka.KafkaCommentTopicConfigurationProperties;
import faang.school.postservice.event.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaCommentTopicConfigurationProperties commentProps;

    public void sendMessage(CommentEvent commentEvent) {
        kafkaTemplate.send(commentProps.getName(), commentEvent);
    }
}
