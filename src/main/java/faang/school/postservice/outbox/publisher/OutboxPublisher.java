package faang.school.postservice.outbox.publisher;

import faang.school.postservice.outbox.entity.OutboxEvent;
import faang.school.postservice.outbox.entity.OutboxEventType;
import faang.school.postservice.outbox.entity.OutboxStatus;
import faang.school.postservice.outbox.repository.OutboxRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final List<OutboxEventPublisher> publishers;

    private Map<OutboxEventType, OutboxEventPublisher> publisherMap;

    @PostConstruct
    void init() {
        publisherMap = new EnumMap<>(OutboxEventType.class);
        for (OutboxEventPublisher publisher : publishers) {
            publisherMap.put(publisher.getType(), publisher);
        }
    }

    @Scheduled(fixedDelayString = "${spring.outbox.fixed-delay-ms}")
    @Transactional
    public void publishPendingEvents() {
        String thisService = "post-service";

        List<OutboxEvent> events = outboxRepository.findByStatusAndSourceService(OutboxStatus.NEW, thisService);

        for (OutboxEvent event : events) {
            try {
                OutboxEventPublisher publisher =
                        publisherMap.get(event.getEventType());

                if (publisher == null) {
                    throw new IllegalStateException(
                            "No OutboxEventPublisher for type " + event.getEventType()
                    );
                }

                publisher.publish(event.getPayload());
                event.setStatus(OutboxStatus.SENT);

            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                event.setStatus(OutboxStatus.FAILED);
            }
        }
    }
}
