package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.cache.PostCacheService;
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
public class KafkaPostViewConsumer {

    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "post-views",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostViewEvent(
            @Payload PostViewEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,  
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            log.info("Received post view event: postId={}, userId={}, viewedAt={}, key={}, topic={}, partition={}, offset={}",
                    event.getPostId(), event.getUserId(), event.getViewedAt(), key, topic, partition, offset);

            validateEvent(event);

            if (key != null && !event.getPostId().toString().equals(key)) {
                log.warn("Key mismatch! postId={}, key={}", event.getPostId(), key);
            }

            boolean updated = postCacheService.incrementPostViews(event.getPostId());
            
            if (updated) {
                log.info("Successfully incremented views for post {}", event.getPostId());
            } else {
                log.debug("Post {} not found in Redis cache, view not incremented", event.getPostId());
            }

            acknowledgment.acknowledge();
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid event received: postId={}, error={}", 
                    event != null ? event.getPostId() : "null", e.getMessage());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing post view event: postId={}, error={}", 
                    event != null ? event.getPostId() : "null", e.getMessage(), e);
            throw new RuntimeException("Failed to process post view event", e);
        }
    }

    private void validateEvent(PostViewEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("PostViewEvent is null");
        }
        if (event.getPostId() == null) {
            throw new IllegalArgumentException("PostId is null");
        }
        if (event.getUserId() == null) {
            throw new IllegalArgumentException("UserId is null");
        }
    }
}

