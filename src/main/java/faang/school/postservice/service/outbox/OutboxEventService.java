package faang.school.postservice.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.outbox.EventStatus;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import faang.school.postservice.repository.outbox.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final Map<EventType, AbstractEventPublisher<?>> eventPublisherMap;
    private final ObjectMapper objectMapper;

    public void saveOutboxEvent(@NonNull OutboxEvent outboxEvent) {
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public void processingEvent(List<OutboxEvent> events) {
        for (OutboxEvent outboxEvent : events) {
            try {
                AbstractEventPublisher<?> eventPublisher = eventPublisherMap.get(outboxEvent.getType());
                if (eventPublisher == null) {
                    log.warn("No publisher registered for type {}", outboxEvent.getType());
                    outboxEvent.setStatus(EventStatus.FAILED);
                } else {

                    Object payload = objectMapper.readValue(outboxEvent.getPayload(), eventPublisher.getEventClass());
                    ((AbstractEventPublisher<Object>) eventPublisher).publish(payload);
                    outboxEvent.setStatus(EventStatus.SUCCESS);
                }
            } catch (Exception e) {
                log.error("Error processing outbox event id = {}", outboxEvent.getId(), e);
                outboxEvent.setStatus(EventStatus.FAILED);
            }

            saveOutboxEvent(outboxEvent);
        }
    }

    @Async
    public void processingBatchEvent(List<OutboxEvent> batchEvents) {
        processingEvent(batchEvents);
    }
}
