package faang.school.postservice.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.FeedHeatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedHeatProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final AtomicBoolean isHeating = new AtomicBoolean(false);

    @Value("${app.kafka.topics.feed-heat.batch-size:1000}")
    private int batchSize;

    @Value("${app.kafka.topics.feed-heat.name:feed-heat}")
    private String feedHeatTopic;

    /**
     * Sends feed heating events to Kafka.
     * Fetches all users from User Service.
     * Protected from concurrent execution.
     */
    @Async("feedHeatExecutor")
    public void sendFeedHeatEvent() {
        if (!isHeating.compareAndSet(false, true)) {
            log.warn("Feed heating already in progress");
            return;
        }
        
        try {

            String jobId = UUID.randomUUID().toString();

            List<Long> allUserIds = getAllUserIdsFromUserService();
            
            if (allUserIds.isEmpty()) {
                log.warn("No users found for feed heating");
                return;
            }
            
            log.info("Starting feed heating job {} for {} users", jobId, allUserIds.size());
            
            int batchNumber = 0;
            int sentBatches = 0;
            int failedBatches = 0;

            for (int i = 0; i < allUserIds.size(); i += batchSize) {
                int end = Math.min(i + batchSize, allUserIds.size());
                List<Long> batch = allUserIds.subList(i, end);
                
                boolean success = sendBatchSync(batch, batchNumber++, jobId);
                if (success) {
                    sentBatches++;
                } else {
                    failedBatches++;
                }

                if ((i + batch.size()) % 100000 == 0) {
                    log.info("Progress: {} users, {} batches sent, {} failed", 
                            i + batch.size(), sentBatches, failedBatches);
                }
            }
            
            log.info("Feed heating job {} completed: {} batches sent, {} failed", 
                    jobId, sentBatches, failedBatches);
            
        } catch (Exception e) {
            log.error("Error sending feed heat events", e);
        } finally {
            isHeating.set(false);
        }
    }

    private List<Long> getAllUserIdsFromUserService() {
        try {
            List<Long> userIds = userServiceClient.getAllUserIds();
            log.info("Fetched {} user IDs from User Service", userIds.size());
            return userIds;
            
        } catch (Exception e) {
            log.error("Error fetching user IDs from User Service", e);
            return List.of();
        }
    }

    /**
     * Checks if feed heating is in progress.
     * 
     * @return true if heating is in progress, false otherwise
     */
    public boolean isHeating() {
        return isHeating.get();
    }

    /**
     * Sends batch synchronously with error handling and timeout.
     */
    private boolean sendBatchSync(List<Long> batch, int batchNumber, String jobId) {
        FeedHeatEvent event = FeedHeatEvent.builder()
            .userIds(batch)
            .batchNumber(batchNumber)
            .jobId(jobId)
            .build();
        
        try {
            kafkaTemplate.send(feedHeatTopic, String.valueOf(batchNumber), event)
                .get(10, TimeUnit.SECONDS);
            
            log.debug("Sent batch {} with {} users", batchNumber, batch.size());
            return true;
        } catch (TimeoutException e) {
            log.error("Timeout sending batch {}", batchNumber);
            return false;
        } catch (Exception e) {
            log.error("Error sending batch {}", batchNumber, e);
            return false;
        }
    }
}
