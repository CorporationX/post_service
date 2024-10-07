package faang.school.postservice.producer.comment;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentProducer extends AbstractProducer<CommentEvent> implements CommentServiceProducer {
    public CommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                           @Value("${kafka.topic.comments-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }

    @Override
    public void send(Comment comment) {
        CommentEvent commentEvent =
                new CommentEvent(comment.getId(), comment.getAuthorId(),
                        comment.getPost().getId(), comment.getContent());

        sendEvent(commentEvent);

    }
}
