package faang.school.postservice.newsfeed.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.newsfeed.KafkaCommentEvent;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.repository.outbox.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxCommentProducer extends AbstractOutboxProducer<KafkaCommentEvent> {

    public OutboxCommentProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
    }

    public void saveToOutbox(KafkaCommentEvent comment) {
        saveToOutbox(AggregateType.COMMENT, comment.postId(), EventType.COMMENT_CREATED, comment);
    }
}
