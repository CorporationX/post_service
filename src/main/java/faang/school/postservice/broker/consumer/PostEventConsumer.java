package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventConsumer {

    private final FeedService feedService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @KafkaListener(
            topics = "${spring.kafka.topic.posts-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "postPublishEventContainerFactory")
    public void consume(PostPublicationEvent postPublicationEvent, Acknowledgment acknowledgment) {
        CompletableFuture.runAsync(() ->
                                feedService.processNewPost(
                                        postPublicationEvent.postId(),
                                        postPublicationEvent.followersIds()),
                        asyncTaskExecutor)
                .thenAccept(res -> {
                    log.info("Post {} publication processed", postPublicationEvent.postId());
                    acknowledgment.acknowledge();
                })
                .exceptionally(exception -> {
                    log.error("Error consuming post publication event with post id {}. Error: {}",
                            postPublicationEvent.postId(), exception.getMessage());
                    return null;
                });
    }
}
