package faang.school.postservice.config.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class PostPublisher {

    private final ExecutorService executorService;
    private final PostService postService;

    /**
     * Задаем тайм-аут на ожидание результата.
     * Проверяем, если список пуст, то дальнейшие операции не требуются.
     * Проверяем, есть ли посты с датой публикации, и обновляем их.
     * Устанавливаем флаг "published" для постов с датой публикации.
     * Асинхронно сохраняем обновленные посты.
     */
    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void checkPostToPublish() {

        Future<List<Post>> postsFromDBFuture = executorService.submit(postService::getAllPostsNotPublished);

        List<Post> postsFromDB;
        try {
            postsFromDB = postsFromDBFuture.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("PostPublisher. Error retrieving posts from DB", e);
            throw new RuntimeException("PostPublisher. Failed to retrieve posts due to async operation failure", e);
        }

        if (postsFromDB.isEmpty()) {
            log.info("No unpublished posts found.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        boolean hasFuturePosts = postsFromDB.stream()
                .anyMatch(post -> post.getPublishedAt().isAfter(now));

        if (hasFuturePosts) {
            postsFromDB.parallelStream().forEach(post -> post.setPublished(true));
        }

        CompletableFuture.runAsync(() -> {
            try {
                postService.savePosts(postsFromDB);
            } catch (Exception e) {
                log.error("PostPublisher. Error save post", e);
                throw new RuntimeException("PostPublisher. Failed to save post due to async operation failure", e);
            }
        }, executorService);
    }
}
