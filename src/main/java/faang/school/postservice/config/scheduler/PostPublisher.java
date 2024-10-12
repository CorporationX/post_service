package faang.school.postservice.config.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class PostPublisher {

    private final ExecutorService executorService;
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void checkPostToPublish() {

       Future<List<Post>> postsFromDB = executorService.submit(postService::getAllPostsNotPublished);

        try {
            postsFromDB.get().forEach(post -> post.setPublished(true));
        } catch (InterruptedException | ExecutionException e) {
            log.error("PostPublisher. Error set publish true", e);
            throw new RuntimeException("PostPublisher. Failed to set publish true due to async operation failure", e);
        }

        try {
            postService.savePosts(postsFromDB.get().stream().toList());
        } catch (InterruptedException | ExecutionException e) {
            log.error("PostPublisher. Error save post", e);
            throw new RuntimeException("PostPublisher. Failed to save post due to async operation failure", e);
        }
    }
}
