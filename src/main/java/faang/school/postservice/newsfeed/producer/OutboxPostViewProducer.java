package faang.school.postservice.newsfeed.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.newsfeed.KafkaPostViewEvent;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.repository.outbox.OutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OutboxPostViewProducer extends AbstractOutboxProducer<KafkaPostViewEvent> {

    public OutboxPostViewProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper);
    }

    public void saveToOutbox(KafkaPostViewEvent view) {
        saveToOutbox(AggregateType.POST_VIEW, view.postId(), EventType.POST_VIEWED, view);
    }
}
