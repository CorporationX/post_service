package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.service.FeedService;
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

    @KafkaListener(topics = "${app.kafka.topics.posts-create-topic}", groupId = "post-feed-consumer-group")
    public void handlePostsCreateEvents(PostEventDto event, Acknowledgment ack) {
        log.info("Received post event: postId={}, followerCount={}",
                event.postId(),
                event.followerIds() != null ? event.followerIds().size() : 0);
        try {
            feedService.updateFeeds(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process post: {}", event.postId(), e);
        }
    }
}