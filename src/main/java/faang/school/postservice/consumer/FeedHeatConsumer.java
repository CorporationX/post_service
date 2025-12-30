package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.FeedHeatEvent;
import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedHeatConsumer {

    private final FeedHeaterService feedHeaterService;

    /**
     * Processes feed heating events from Kafka.
     * Each server processes its own batch of users, allowing load distribution.
     */
    @KafkaListener(
            topics = "${app.kafka.topics.feed-heat.name:feed-heat}",
            containerFactory = "feedHeatListenerContainerFactory"
    )
    public void consumeFeedHeatEvent(
            @Payload FeedHeatEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            validateEvent(event);
            
            log.info("Processing batch {}: {} users (job={}, partition={}, offset={})",
                    event.getBatchNumber(), event.getUserIds().size(), 
                    event.getJobId(), partition, offset);

            feedHeaterService.heatFeedForUsers(event.getUserIds(), event.getJobId());
            
            log.info("Completed batch {}: {} users processed (job={})", 
                    event.getBatchNumber(), event.getUserIds().size(), event.getJobId());
            
            acknowledgment.acknowledge();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid feed heat event: batchNumber={}, error={}", 
                    event != null ? event.getBatchNumber() : "null", e.getMessage());
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing batch {}: {}", 
                    event != null ? event.getBatchNumber() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to process feed heat event", e);
        }
    }

    private void validateEvent(FeedHeatEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("FeedHeatEvent is null");
        }
        if (event.getUserIds() == null || event.getUserIds().isEmpty()) {
            throw new IllegalArgumentException("User IDs list is empty");
        }
        if (event.getBatchNumber() < 0) {
            throw new IllegalArgumentException("Invalid batch number: " + event.getBatchNumber());
        }
        if (event.getJobId() == null || event.getJobId().isBlank()) {
            throw new IllegalArgumentException("Job ID is required for idempotency");
        }
    }
}