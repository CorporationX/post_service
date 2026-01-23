package faang.school.postservice.service.outbox;

import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.model.outbox.OutboxStatus;
import faang.school.postservice.repository.OutboxEventRepository;
import faang.school.postservice.service.outbox.handlers.OutboxEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OutboxDispatchService {

    private final OutboxEventRepository outboxEventRepository;
    private final Map<String, OutboxEventHandler> handlersByType;

    // Spring will inject List<OutboxEventHandler>, so we build a map once
    public OutboxDispatchService(OutboxEventRepository outboxEventRepository,
                                 List<OutboxEventHandler> handlers) {
        this.outboxEventRepository = outboxEventRepository;
        this.handlersByType = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        OutboxEventHandler::eventType,
                        Function.identity()
                ));
    }

    @Transactional
    public void dispatchOnce(int batchSize) {
        List<OutboxEvent> batch = outboxEventRepository.pickBatchForUpdateSkipLocked(batchSize);
        if (batch.isEmpty()) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        batch.forEach(e -> {
            e.setStatus(OutboxStatus.PROCESSING);
            e.setUpdatedAt(now);
        });

        for (OutboxEvent e : batch) {
            try {
                OutboxEventHandler handler = handlersByType.get(e.getEventType());
                if (handler == null) {
                    throw new IllegalStateException("No handler for eventType=" + e.getEventType());
                }

                handler.handle(e);

                OffsetDateTime sentNow = OffsetDateTime.now();
                e.setStatus(OutboxStatus.SENT);
                e.setUpdatedAt(sentNow);
                e.setSentAt(sentNow);

            } catch (Exception ex) {
                OffsetDateTime failNow = OffsetDateTime.now();
                e.setAttempts(e.getAttempts() + 1);
                e.setLastError(shortErr(ex));
                e.setStatus(OutboxStatus.FAILED);
                e.setUpdatedAt(failNow);
                e.setSentAt(null);

                log.error("Failed to dispatch outbox event id={} type={}", e.getEventId(), e.getEventType(), ex);
            }
        }
        // JPA flushes on commit
    }

    private String shortErr(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            return ex.getClass().getSimpleName();
        }
        return msg.length() > 500 ? msg.substring(0, 500) : msg;
    }
}
