package faang.school.postservice.publishers.kafka;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publishers.AbstractKafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventKafkaPublisher extends AbstractKafkaMessagePublisher<Comment, CommentEvent> {
    private final CommentMapper commentMapper;

    public CommentEventKafkaPublisher(@Value("${kafka.topics.comment_event}") String topic
            , KafkaTemplate<String, CommentEvent> commentEventKafkaTemplate,
                                      CommentMapper commentMapper) {
        super(topic, commentEventKafkaTemplate);
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentEvent mapper(Comment comment) {
        return commentMapper.toEvent(comment);
    }
}
