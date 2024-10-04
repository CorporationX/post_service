package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BanUserService {
    private static final int SIZE_OF_BATCH = 1000;
    private static final int NUMBER_OF_THREADS = 4;

    @Value("${redis.channels.user-ban}")
    String bannedUserTopic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;

    public void banAuthorsWithUnverifiedPosts() {
        Long maxUserId = userServiceClient.getMaxUserId();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (long minId = 1; minId < maxUserId; minId += SIZE_OF_BATCH) {
            long maxId = Math.min(minId + SIZE_OF_BATCH - 1, maxUserId);
            long finalMinId = minId;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    List<Long> authorIdsToBan = postRepository.findAuthorsWithMoreThanFiveUnverifiedPostsInRange(finalMinId, maxId);
                    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                        authorIdsToBan.forEach(authorId -> connection.publish(bannedUserTopic.getBytes(), String.valueOf(authorId).getBytes()));
                        return null;
                    });
                } catch (Exception e) {
                    log.error("While checking and banning users from id {} to {} an exception occurred", finalMinId, maxId, e);
                }
            }, executor);
            futures.add(future);
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();

        executor.shutdown();
    }
}
