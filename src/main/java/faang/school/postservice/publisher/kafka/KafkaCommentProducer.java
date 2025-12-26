package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.comment.name:comment-topic}")
    private String commentTopicName;

    public void sendCommentEvent(CommentEvent event) {
        kafkaTemplate.send(commentTopicName, event);
        log.info("Comment event sent to kafka: {}", event);
    }
}
