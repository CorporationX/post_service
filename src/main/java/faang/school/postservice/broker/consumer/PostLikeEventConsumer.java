package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.post.PostLikeEvent;
import faang.school.postservice.service.PostService;
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
public class PostLikeEventConsumer {

    private final PostService postService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @KafkaListener(
            topics = "${spring.kafka.topic.post-likes-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "postLikeEventContainerFactory")
    public void consume(PostLikeEvent postLikeEvent, Acknowledgment acknowledgment) {
        long postId = postLikeEvent.postId();
        CompletableFuture.runAsync(() ->
                                postService.incrementPostLikesCounter(postId),
                        asyncTaskExecutor)
                .thenAccept(res -> {
                    log.info("Post {} like processed", postId);
                    acknowledgment.acknowledge();
                })
                .exceptionally(exception -> {
                    log.error("Error consuming post like event with post id {}. Error: {}",
                            postId, exception.getMessage());
                    return null;
                });
    }
}
