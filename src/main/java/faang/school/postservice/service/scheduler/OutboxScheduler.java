package faang.school.postservice.service.scheduler;

import faang.school.postservice.model.outbox.EventStatus;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.repository.outbox.OutboxEventRepository;
import faang.school.postservice.service.outbox.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    @Value("${scheduler.outbox-event.batch-size}")
    private int batchSize;
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventService outboxEventService;

    @Scheduled(fixedDelayString = "${post-corrector.fixed-delay.outbox-event.every-five-second}")
    public void OutboxEventsProcessed() {
        for (EventType eventType : EventType.values()) {
            List<OutboxEvent> events = outboxEventRepository
                    .findTop100ByStatusAndTypeOrderByCreatedAtAsc(EventStatus.IN_PROGRESS, eventType);

            for (int i = 0; i < events.size(); i += batchSize) {
                List<OutboxEvent> batch = events.subList(i, Math.min(i + batchSize, events.size()));
                outboxEventService.processingBatchEvent(batch);
            }
        }
    }
}
