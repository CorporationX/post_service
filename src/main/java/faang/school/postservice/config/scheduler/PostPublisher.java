package faang.school.postservice.config.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.apache.commons.collections4.ListUtils.partition;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class PostPublisher {

    private final ExecutorService executorService;
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void checkPostToPublish() {
        CompletableFuture
                .supplyAsync(this::getUnpublishedPosts, executorService)
                .thenAcceptAsync(this::publishAndSavePosts, executorService)
                .exceptionally(ex -> {
                    log.error("PostPublisher. Error occurred during post publishing", ex);
                    return null;
                });
    }

    private List<Post> getUnpublishedPosts() {
        try {
            return postService.getAllPostsNotPublished();
        } catch (Exception e) {
            throw new RuntimeException(
                    "PostPublisher. Failed to retrieve posts due to async operation failure", e);
        }
    }

    private void publishAndSavePosts(List<Post> postsFromDB) {
        if (postsFromDB.isEmpty()) {
            log.info("No unpublished posts found.");
            return;
        }

        postsFromDB.forEach(post -> post.setPublished(true));

        List<List<Post>> subLists = partition(postsFromDB, 10);

        List<CompletableFuture<Void>> futures = subLists.stream()
                .map(subList -> CompletableFuture.runAsync(() -> savePostBatch(subList), executorService)
                        .exceptionally(ex -> {
                            log.error("PostPublisher. Error saving post batch", ex);
                            return null;
                        }))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.join();
            log.info("All post batches processed successfully.");
        } catch (Exception ex) {
            log.error("PostPublisher. Some post batches failed during processing.", ex);
        }
    }

    private void savePostBatch(List<Post> subList) {
        try {
            postService.savePosts(subList);
        } catch (Exception e) {
            log.error("PostPublisher. Error saving post batch", e);
            throw new RuntimeException(
                    "PostPublisher. Failed to save post batch due to async operation failure", e);
        }
    }
}