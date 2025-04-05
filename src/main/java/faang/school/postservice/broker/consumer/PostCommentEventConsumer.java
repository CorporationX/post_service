package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.post.PostCommentEvent;
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
public class PostCommentEventConsumer {

    private final PostService postService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @KafkaListener(
            topics = "${spring.kafka.topic.post-comments-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "postCommentEventContainerFactory")
    public void consume(PostCommentEvent postCommentEvent, Acknowledgment acknowledgment) {
        long postId = postCommentEvent.postId();
        CompletableFuture.runAsync(() ->
                                postService.addCommentToHash(postId, postCommentEvent),
                        asyncTaskExecutor)
                .thenAccept(res -> {
                    log.info("Post {} comment processed", postId);
                    acknowledgment.acknowledge();
                })
                .exceptionally(exception -> {
                    log.error("Error consuming comment event with post id {}. Error: {}",
                            postId, exception.getMessage());
                    return null;
                });
    }
}
