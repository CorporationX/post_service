package faang.school.postservice.kafka.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.CacheWarmupTask;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmupListener {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(
            topics = "${spring.kafka.topic.feed_warmer}",
            concurrency = "${cache.feed.warm-up-concurrency}")
    public void listen(String message) {
        try {
            CacheWarmupTask task = objectMapper.readValue(message, CacheWarmupTask.class);
            log.info("Warming up cache for users: {}", task.subscriberIds());
            feedService.warmUp(task.subscriberIds());
        } catch (Exception e) {
            log.error("Failed to process warmup task", e);
            throw new RuntimeException(e); // triggers retry / error handling
        }
    }
}

