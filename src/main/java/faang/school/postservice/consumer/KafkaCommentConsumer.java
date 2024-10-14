package faang.school.postservice.consumer;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.comment.name}",
            groupId = "${spring.data.kafka.consumer.groups.comment}")
    public void consume(FeedCommentEvent event) {
        Long postId = event.getPostId();
        log.info("Received FeedCommentEvent for post ID: {}", postId);

        try {
            feedService.updatePostComments(postId, event);
            log.info("Successfully updated comments for post ID: {}", postId);
        } catch (Exception e) {
            log.error("Failed to update comments for post ID: {}", postId, e);
        }
    }
}
