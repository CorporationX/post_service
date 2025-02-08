package faang.school.postservice.event_sender;

import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventSender {
    private final CommentEventMapper commentEventMapper;
    private final KafkaCommentProducer kafkaCommentProducer;

    public void sendEvent(Comment comment) {
        CommentEvent commentEvent = commentEventMapper.toEvent(comment);
        kafkaCommentProducer.send(commentEvent);

        log.debug("Event comment with id {} successfully sent to Kafka", comment.getId());
    }
}
