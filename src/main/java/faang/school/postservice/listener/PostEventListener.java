
package faang.school.postservice.listener;

import faang.school.postservice.cache.FeedCacheRepository;
import faang.school.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final FeedCacheRepository feedCacheRepository;

    @KafkaListener(topics = "${kafka.topic.post}")
    public void handlePostReceivedEvent(PostEvent jsonEvent, Acknowledgment acknowledgment) {
        try {
            log.info("Successfully listen event from a post topic: {}", jsonEvent);

            if (jsonEvent.followersIds() != null && !jsonEvent.followersIds().isEmpty())
                for (Long followersId : jsonEvent.followersIds()) {
                    feedCacheRepository.save(followersId, jsonEvent.postId(), jsonEvent.createdAt());
                }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process post event {}", jsonEvent, e);
        }
    }
}
