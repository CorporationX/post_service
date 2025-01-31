package faang.school.postservice.consumer;

import faang.school.postservice.event.PostPublishedEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topics.post-published}")
    public void listen(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received post published event from Kafka: {}", event);
        try {
            event.getFollowersId().forEach(followerId -> {
                        try {
                            feedService.bindPostToFollower(followerId, event.getPostId());
                        } catch (Exception e) {
                            log.error("Failed to process follower {} for post {}", followerId, event.getPostId(), e);
                        }
                    }
            );
            ack.acknowledge();
            log.info("Kafka post event consumer finished for post with id {}", event.getPostId());
        } catch (Exception ex) {
            log.error("Critical error processing event {}", event, ex);
            throw ex;
        }
    }
}
