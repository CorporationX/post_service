package faang.school.postservice.consumer.kafka;

import faang.school.postservice.config.properties.FeedCacheProperties;
import faang.school.postservice.dto.event.PostCreatedKafkaEvent;
import faang.school.postservice.service.feed.FeedCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final FeedCachePort feedCachePort;
    private final FeedCacheProperties feedCacheProperties;

    @KafkaListener(
            topics = "#{appKafkaProperties.topics.posts}",
            containerFactory = "postEventKafkaListenerContainerFactory"
    )
    public void onPostCreated(PostCreatedKafkaEvent event, Acknowledgment ack) {
        try {
            if (event == null || event.getFollowerIds() == null) {
                ack.acknowledge();
                return;
            }
            long score = event.getPublishedAtEpochMillis();
            for (Long followerId : event.getFollowerIds()) {
                if (followerId == null) continue;
                feedCachePort.addToFeed(followerId, event.getPostId(), score);
                feedCachePort.trimToMaxSize(followerId, feedCacheProperties.getMaxSize());
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing post-created event for post {}: {}", 
                    event != null ? event.getPostId() : null, e.getMessage(), e);
        }
    }
}
