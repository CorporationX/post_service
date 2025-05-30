package faang.school.postservice.publisher;

import faang.school.postservice.exception.OutboxPublishException;
import faang.school.postservice.mapper.OutboxEventMapper;
import faang.school.postservice.model.outbox.OutboxFeedEvent;
import faang.school.postservice.repository.outbox.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private static final int THRESHOLD_DAYS = 7;
    private static final int PAGE_SIZE = 30;
    private static final int MAX_PARALLELISM = 10;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final OutboxEventMapper outboxEventMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_PARALLELISM);

    @Scheduled(fixedDelayString = "${outbox.publisher.interval:5000}")
    @Transactional
    public void publishEvents() {
        int pageNumber = 0;
        List<OutboxFeedEvent> eventsPage;
        do {
            Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
            eventsPage = outboxRepository.findUnprocessedEvents(pageable);
            if (!eventsPage.isEmpty()) {
                log.info("Publishing {} events from page {}", eventsPage.size(), pageNumber);
                List<CompletableFuture<Void>> futures = eventsPage.stream()
                        .map(event -> CompletableFuture.runAsync(() -> processSingleEvent(event), executor))
                        .toList();
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
            pageNumber++;
        } while (eventsPage.size() == PAGE_SIZE);
    }

    @Scheduled(cron = "${outbox.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public void cleanupProcessedEvents() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(THRESHOLD_DAYS);
        int deleted = outboxRepository.deleteOldProcessed(threshold);
        log.info("Deleted {} old processed outbox events older than {}", deleted, threshold);
    }

    @Retryable(
            retryFor = OutboxPublishException.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void processSingleEvent(OutboxFeedEvent event) {
        try {
            String topic = resolveTopic(event);
            String key = resolveKey(event);
            String payload = resolvePayload(event);
            sendToKafka(topic, key, payload);
            markAsProcessed(event);
            log.debug("Successfully published event {} to topic {}", event.getId(), topic);
        } catch (Exception e) {
            log.error("Failed to publish event {} after retries", event.getId(), e);
        }
    }

    private String resolveTopic(OutboxFeedEvent event) {
        return outboxEventMapper.mapToTopic(event);
    }

    private String resolveKey(OutboxFeedEvent event) {
        return event.getAggregateId().toString();
    }

    private String resolvePayload(OutboxFeedEvent event) {
        return event.getPayload();
    }

    private void sendToKafka(String topic, String key, String payload) throws Exception {
        kafkaTemplate.send(topic, key, payload).get();
    }

    private void markAsProcessed(OutboxFeedEvent event) {
        event.setProcessed(true);
        event.setProcessedAt(LocalDateTime.now());
        outboxRepository.save(event);
    }
}
