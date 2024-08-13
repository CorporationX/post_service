package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PostPublishService {

    @Async("schedulerExecutor")
    public CompletableFuture<List<Post>> publishBatch(List<Post> batch) {
        for (Post post : batch) {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        return CompletableFuture.completedFuture(batch);
    }
}
