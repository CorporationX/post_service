package faang.school.postservice.job.moderation;

import faang.school.postservice.exception.PostModeratingProcessorException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostModeratingProcessor {

    private final PostModerator postModerator;

    public CompletableFuture<Void> processBatch(List<Post> batch) {
        return CompletableFuture.runAsync(() -> {
            validateBatch(batch);
            batch.forEach(this::processPost);
        });
    }

    private void processPost(Post post) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("{}: processing post {}", Thread.currentThread().getName(), post.getId());
            int min = 5;
            int max = 10;
            int randomNum = (int)(Math.random() * (max - min + 1)) + min;
            Thread.sleep(randomNum*1000);
            postModerator.handle(post);
            long endTime = System.currentTimeMillis();
            log.info("{}: Post {} processed in {} ms", Thread.currentThread().getName(), post.getId(), endTime - startTime);
        } catch (Exception e) {
            log.error("Error processing post with ID {}: {}", post.getId(), e.getMessage(), e);
            throw new PostModeratingProcessorException("Error processing post with ID " + post.getId(), e);
        }
    }

    private void validateBatch(List<Post> batch) {
        if (batch == null || batch.isEmpty()) {
            log.info("Batch is null or empty");
            throw new IllegalArgumentException("Batch cannot be null or empty");
        }
    }

}