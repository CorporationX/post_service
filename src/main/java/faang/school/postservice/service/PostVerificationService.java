package faang.school.postservice.service;

import faang.school.postservice.publisher.AuthorBanPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostVerificationService {

    private final AuthorBanPublisher banPublisher;

    @Qualifier("authorBannerPool")
    private final ThreadPoolTaskExecutor authorBannerExecutor;

    private final PostRepository postRepository;

    @Value("${ban-properties.value-rejected-posts}")
    private int valuePosts;

    @Value("${thread-pool.author-ban.size}")
    private int banAuthorPoolSize;

    @Async("authorBannerPool")
    public void checkAuthorsPostsVerification() {
        List<Long> authorsIdsToBan = postRepository.findAuthorIdsWithMinRejectedPosts(valuePosts);
        int actualPoolSize = Math.max(1, banAuthorPoolSize);
        int tasksPerThread = Math.max(1, authorsIdsToBan.size() / actualPoolSize);
        List<List<Long>> chunks = ListUtils.partition(authorsIdsToBan, tasksPerThread);

        List<CompletableFuture<Void>> tasks = chunks.stream()
                .map(chunk -> CompletableFuture.runAsync(
                        () -> chunk.forEach(banPublisher::publish),
                        authorBannerExecutor
                ))
                .toList();

        try {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
            log.debug("Authors posts verification completed. All threads interrupted.");
        } catch (CompletionException e) {
            log.error("Check posts verification error {}", e.toString());
        }
    }
}
