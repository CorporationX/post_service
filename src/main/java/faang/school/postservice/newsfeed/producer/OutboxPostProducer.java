package faang.school.postservice.newsfeed.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.newsfeed.KafkaPostEvent;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.repository.outbox.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxPostProducer extends AbstractOutboxProducer<KafkaPostEvent> {

    public OutboxPostProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
    }

    public void saveToOutbox(KafkaPostEvent post) {
        saveToOutbox(AggregateType.POST, post.postId(), EventType.POST_CREATED, post);
    }
}
