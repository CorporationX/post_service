package faang.school.postservice.newsfeed.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.newsfeed.KafkaLikeEvent;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.repository.outbox.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxLikeProducer extends AbstractOutboxProducer<KafkaLikeEvent> {

    public OutboxLikeProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
    }

    public void saveToOutbox(KafkaLikeEvent like) {
        saveToOutbox(AggregateType.LIKE, like.postId(), EventType.LIKE_CREATED, like);
    }
}
