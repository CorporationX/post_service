package faang.school.postservice.listener;

import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
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
    private final PostCacheRepository postCacheRepository;

    @KafkaListener(
            topics = "${kafka.topic.post-event}",
            containerFactory = "concurrentKafkaPostListenerFactory")
    public void handlePostPublishEvent(PostPublishEventDto postPublishEventDto, Acknowledgment acknowledgment) {
        log.info("New post publish event: {}", postPublishEventDto);
        try {
            postCacheRepository.save(PostCacheDto.builder()
                    .id(postPublishEventDto.postId())
                    .authorId(postPublishEventDto.authorId())
                    .content(postPublishEventDto.content())
                    .createdAt(Instant.now())
                    .build());

            if (postPublishEventDto.subscriberIds() != null
                    && !postPublishEventDto.subscriberIds().isEmpty()) {
                for (Long subscriberId : postPublishEventDto.subscriberIds()) {
                    feedCacheRepository.save(subscriberId,
                            postPublishEventDto.postId(),
                            Instant.now());
                }
            }
        } catch (Exception exception) {
            log.error("Failed to process a post event {}", postPublishEventDto, exception);
            return;
        }
        acknowledgment.acknowledge();
    }
}
