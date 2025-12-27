package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final FeedService feedService;

    @KafkaListener(
            topics = "${app.kafka.topics.posts-created-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPostCreated(
            PostCreatedEventDto event,
            Acknowledgment ack
    ) {
        log.info(
                "Received PostCreatedEvent: postId={}, followers={}",
                event.postId(),
                event.followerIds().size()
        );

        try {
            event.followerIds().forEach(
                    followerId -> feedService.addPostToFeed
                            (
                                    followerId,
                                    event.postId(),
                                    event.occurredAt()
                            ));

            ack.acknowledge();

            log.info(
                    "Post {} added to {} feeds, ack sent",
                    event.postId(),
                    event.followerIds().size()
            );
        } catch (Exception e) {
            log.error(
                    "Failed to process PostCreatedEvent postId={}",
                    event.postId(),
                    e
            );
            throw e;
        }
    }
}