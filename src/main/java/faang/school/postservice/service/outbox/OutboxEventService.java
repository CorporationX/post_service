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
import org.springframework.scheduling.annotation.Scheduled;
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

    @Transactional
    public void saveOutboxEvent(@NonNull OutboxEvent outboxEvent) {
        outboxEventRepository.save(outboxEvent);
    }

    @Scheduled(fixedDelayString = "${post-corrector.fixed-delay.outbox-event.every-five-second}")
    @Transactional
    public void publishOutboxEvents() {
        for (EventType eventType : EventType.values()) {
            processOutboxEvent(eventType);
        }
    }


    @Transactional
    public void processOutboxEvent(EventType eventType) {
        List<OutboxEvent> outboxEvents = outboxEventRepository
                .findTop100ByStatusAndTypeOrderByCreatedAtAsc(EventStatus.IN_PROGRESS, eventType);

        for (OutboxEvent outboxEvent : outboxEvents) {
            try {
                AbstractEventPublisher<?> eventPublisher = eventPublisherMap.get(eventType);
                if (eventPublisher == null) {
                    log.warn("No handler for event type {}", eventType);
                    continue;
                }

                Object payload = objectMapper.readValue(outboxEvent.getPayload(), eventPublisher.getEventClass());
                ((AbstractEventPublisher<Object>) eventPublisher).publish(payload);
                outboxEvent.setStatus(EventStatus.SUCCESS);
                saveOutboxEvent(outboxEvent);


            } catch (Exception e) {
                log.error("Error processing outbox event id = {}", outboxEvent.getId(), e);
                outboxEvent.setStatus(EventStatus.FAILED);
                saveOutboxEvent(outboxEvent);
            }
        }
    }
}
