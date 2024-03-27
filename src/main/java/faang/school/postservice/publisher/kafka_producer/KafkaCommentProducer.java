package faang.school.postservice.publisher.kafka_producer;

import faang.school.postservice.dto.kafka_events.CommentKafkaEvent;
import faang.school.postservice.model.Comment;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentKafkaEvent> {
    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishCommentKafkaEvent(Comment comment) {
        CommentKafkaEvent commentKafkaEvent = new CommentKafkaEvent(comment);
        publishKafkaEvent(commentKafkaEvent, commentTopic);
    }
}
