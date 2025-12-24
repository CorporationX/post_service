package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventListener {
    private final FeedCacheRepository feedCacheRepository;

    @KafkaListener(
            topics = "${kafka.topic.post-event}",
            containerFactory = "concurrentKafkaPostListenerFactory")
    public void handlePostPublishEvent(PostPublishEventDto postPublishEventDto, Acknowledgment acknowledgment) {
        log.info("New post publish event: {}", postPublishEventDto);
        try {
            if (postPublishEventDto.subscriberIds() != null
                    && !postPublishEventDto.subscriberIds().isEmpty()) {
                for (Long subscriberId : postPublishEventDto.subscriberIds()) {
                    feedCacheRepository.save(subscriberId,
                            postPublishEventDto.postId(),
                            Instant.now());
                }
                acknowledgment.acknowledge();
            }
        } catch (Exception exception) {
            log.error("Failed to process a post event {}", postPublishEventDto, exception);
        }
    }
}
