package faang.school.postservice.consumer.kafka;

import faang.school.postservice.publisher.kafka.events.PostEvent;
import faang.school.postservice.service.redis.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostConsumer {
    private final FeedService feedService;
    @KafkaListener(
            topics = "${spring.kafka.topic-name.posts}",
            groupId = "post-group"
    )
    public void consume(PostEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received PostEvent: {}", event);

            feedService.addPostToFollowersFeed(
                    event.getPostId(),
                    event.getFollowersIds(),
                    event.getPublishedAt()
            );
            acknowledgment.acknowledge();
            log.info("Acknowledged PostEvent for post ID {}", event.getPostId());

        } catch (Exception e) {
            log.error("Failed to process PostEvent for post ID {}: {}", event.getPostId(), e.getMessage());
        }
    }
}
