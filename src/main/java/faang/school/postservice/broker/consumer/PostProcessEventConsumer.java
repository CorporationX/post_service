package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.post.PostProcessEvent;
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
public class PostProcessEventConsumer {

    private final FeedService feedService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @KafkaListener(
            topics = "${spring.kafka.topic.posts-process-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "postPublishEventContainerFactory")
    public void consume(PostProcessEvent postProcessEvent, Acknowledgment acknowledgment) {
        CompletableFuture.runAsync(() ->
                                feedService.subProcessNewPost(
                                        postProcessEvent.postId(),
                                        postProcessEvent.followersIds()),
                        asyncTaskExecutor)
                .thenAccept(res -> {
                    log.info("Post {} followers batch processed", postProcessEvent.postId());
                    acknowledgment.acknowledge();
                })
                .exceptionally(exception -> {
                    log.error("Error consuming post followers batch processing event with post id {}. Error: {}",
                            postProcessEvent.postId(), exception.getMessage());
                    return null;
                });
    }
}
