package faang.school.postservice.consumer.kafka;

import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import faang.school.postservice.service.redis.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeatConsumer {
    private final FeedService feedService;

    @KafkaListener(
            topics = "${spring.kafka.topic-name.heat-feed}",
            groupId = "cache-heat-group"
    )
    public void consume(FeedHeatEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received FeedHeatEvent for users: {}", event.getUserIds());
            feedService.processCacheHeatEvent(event);

            acknowledgment.acknowledge();
            log.info("Acknowledged FeedHeatEvent for users: {}", event.getUserIds());
        } catch (Exception e) {
            log.error("Failed to process FeedHeatEvent: {}", e.getMessage(), e);
        }
    }
}
