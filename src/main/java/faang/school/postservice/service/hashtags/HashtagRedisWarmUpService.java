package faang.school.postservice.service.hashtags;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashtagRedisWarmUpService {
    private static final int TIMEOUT_HOURS = 1;

    @Value("${app.hashtags.max-cached-posts-per-hashtag}")
    private int maxCachedPosts;

    @Value("${app.hashtags.thread-pool-size}")
    private int threadPoolSize;

    @Value("${app.hashtags.top-hashtags-cache}")
    private int topHashtagsCache;

    private final RedisTemplate<String, String> redisTemplate;
    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final HashtagRedisService hashtagRedisService;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void warmUpCache() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        log.info("Hashtags cache warm-up started");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int size = (int) hashtagRepository.count();
        if (size == 0) {
            log.info("Nothing was added to cache");
            return;
        }
        Pageable pageable = PageRequest.of(0, Math.min(size, topHashtagsCache));
        List<Hashtag> hashtagsToWarmUp = hashtagRepository.getTopHashtags(pageable);
        for (Hashtag hashtag : hashtagsToWarmUp) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Pageable postPageable = PageRequest.of(0, maxCachedPosts);
                List<Post> posts = postRepository.findPostsByHashtag(postPageable, hashtag.getTag()).getContent();
                posts.forEach(post -> hashtagRedisService.saveHashtag(hashtag.getTag(), post));
            }, executor);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .orTimeout(TIMEOUT_HOURS, TimeUnit.HOURS)
                .exceptionally(exception -> {
                    log.error("Warm up was not completed on time", exception);
                    executor.shutdownNow();
                    return null;
                })
                .thenRun(() -> log.info("Hashtags cache warm-up completed successfully"));
    }
}