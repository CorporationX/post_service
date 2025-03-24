package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostSchedulerService {
    private final PostRepository postRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${post.schedule.max-retries}")
    private int maxRetries;

    @Value("${post.schedule.timeout}")
    private int timeout;

    @Value("${post.schedule.initial-delay}")
    private long delay;

    @Value("${post.schedule.delay-multiplier}")
    private long delayMultiplier;

    @Value("${post.schedule.batchCount}")
    private int batchCount;

    public void publishScheduledPosts(int batchSize) {
        Page<Post> page = postRepository.findReadyToPublish(PageRequest.of(0, batchSize));

        if (page.getContent().isEmpty()) {
            log.info("Нет постов для публикации.");
            return;
        }

        int totalPages = page.getTotalPages();
        List<Future<List<Post>>> futures = new ArrayList<>();

        futures.add(threadPoolTaskExecutor.submit(() -> processBatch(page.getContent())));


        for (int i = 1; i < totalPages; i++) {
            Page<Post> postPage = postRepository.findReadyToPublish(PageRequest.of(i, batchSize));

            if (postPage.isEmpty()) continue;

            futures.add(threadPoolTaskExecutor.submit(() -> processBatch(postPage.getContent())));


            if (futures.size() % batchCount == 0) {
                saveProcessedBatches(futures);
            }
        }
        saveProcessedBatches(futures);
    }

    private void saveProcessedBatches(List<Future<List<Post>>> futures) {
        List<Post> processedPosts = futures.stream()
                .map(this::getFutureResult)
                .flatMap(List::stream)
                .toList();

        if (!processedPosts.isEmpty()) {
            postRepository.saveAll(processedPosts);
        }

        futures.clear();
    }

    private List<Post> processBatch(List<Post> posts) {
        List<Post> processedPosts = new ArrayList<>();
        for (Post post : posts) {
            try {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
                processedPosts.add(post);
            } catch (Exception e) {
                log.error("Ошибка обработки поста с ID {}", post.getId(), e);
            }
        }
        return processedPosts;
    }

    private List<Post> getFutureResult(Future<List<Post>> future) {
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                return future.get(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Публикация постов прервана", e);
            } catch (ExecutionException e) {
                log.error("Ошибка при публикации постов", e);
                break;
            } catch (TimeoutException e) {
                log.warn("Превышено время ожидания публикации постов. Попытка {} из {}", attempt + 1, maxRetries);
                attempt++;

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                delay *= delayMultiplier;
            }
        }

        log.error("Не удалось получить результат публикации после {} попыток", maxRetries);
        return Collections.emptyList();
    }
}
