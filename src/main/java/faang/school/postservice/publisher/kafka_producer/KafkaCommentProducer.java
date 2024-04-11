package faang.school.postservice.publisher.kafka_producer;

import faang.school.postservice.dto.kafka_events.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentKafkaEvent> {
    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;

    public void publishCommentKafkaEvent(Comment comment) {
        CommentKafkaEvent commentKafkaEvent = new CommentKafkaEvent(comment);
        publishKafkaEvent(commentKafkaEvent, commentTopic);
    }
}
