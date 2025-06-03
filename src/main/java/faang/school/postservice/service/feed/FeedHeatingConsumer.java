package faang.school.postservice.service.feed;

import faang.school.postservice.exception.CacheOperationException;
import faang.school.postservice.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeatingConsumer {
    private final FeedCacheService feedCacheService;

    @Qualifier("threadPoolExecutor")
    private final ExecutorService processingExecutor;

    @Value("${app.heating.max-queue-size:10000}")
    private int maxQueueSize;

    @KafkaListener(topics = "${spring.kafka.topics.feed-heating}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Long userId) {
        if (userId == null) {
            log.warn("Received null user ID in heating task");
            return;
        }

        if (((ThreadPoolExecutor) processingExecutor).getQueue().size() > maxQueueSize) {
            log.warn("Heating task queue is full. Skipping user {}", userId);
            return;
        }

        processingExecutor.submit(() -> {
            try {
                log.debug("Processing heating task for user: {}", userId);
                feedCacheService.warmUpCacheForUser(userId);
                log.debug("Completed heating task for user: {}", userId);
            } catch (ServiceUnavailableException e) {
                log.warn("Service unavailable for user {}: {}", userId, e.getMessage());
            } catch (CacheOperationException e) {
                log.error("Cache operation failed for user {}: {}", userId, e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error processing user {}", userId, e);
            }
        });
    }
}
