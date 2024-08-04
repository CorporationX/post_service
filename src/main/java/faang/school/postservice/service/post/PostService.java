package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ExecutorService executorService;
    @Value("${post.publisher.batch-size:1000}")
    private int postsBatchSize;

    public void publishScheduledPosts() {
        log.info("Start publishing posts, at: {}", LocalDateTime.now());
        List<Post> posts = postRepository.findReadyToPublish();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < posts.size(); i += postsBatchSize) {
            int end = Math.min(i + postsBatchSize, posts.size());
            List<Post> batch = posts.subList(i, end);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> publishBatch(batch), executorService);
            futures.add(future);
        }
        CompletableFuture<Void> allOfFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOfFutures.join();
        log.info("All posts successful published, at: {}", LocalDateTime.now());
    }

    private void publishBatch(List<Post> batch) {
        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        postRepository.saveAll(batch);
    }
}
