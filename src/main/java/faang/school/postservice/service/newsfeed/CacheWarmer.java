package faang.school.postservice.service.newsfeed;

import faang.school.postservice.client.SubscriptionClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.FeedProperties;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmer {

    private final UserServiceClient userServiceClient;
    private final SubscriptionClient subscriptionClient;
    private final PostRepository postRepository;
    private final RedisCacheService redisCacheService;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final FeedProperties feedProperties;

    @Async
    public void warmUpCache() {
        log.info("Cache warming process started.");
        List<UserDto> allUsersToProcess = fetchAllUsersToWarmCache();

        if (allUsersToProcess.isEmpty()) {
            log.info("No users found to warm cache for. Cache warming process finished.");
            return;
        }

        log.info("Starting cache warming for {} users in total.", allUsersToProcess.size());

        runCacheWarmingAsync(allUsersToProcess)
                .thenRun(() -> log.info("Cache warming completed for all processed users."))
                .exceptionally(ex -> {
                    log.error("Error during cache warming completion: ", ex);
                    return null;
                });
    }

    private List<UserDto> fetchAllUsersToWarmCache() {
        List<UserDto> result = new ArrayList<>();
        int page = 0;
        int pageSize = feedProperties.getCacheWarmerUserBatchSize();

        while (true) {
            log.info("Fetching users page: {}, size: {}", page, pageSize);
            try {
                List<UserDto> userBatch = userServiceClient.getUsersByPage(page, pageSize);
                if (userBatch == null || userBatch.isEmpty()) {
                    log.info("No more users found or empty batch received at page {}.", page);
                    break;
                }
                result.addAll(userBatch);
                page++;
            } catch (Exception e) {
                log.error("Error fetching users at page {}: {}", page, e.getMessage(), e);
                break;
            }
        }

        return result;
    }

    private CompletableFuture<Void> runCacheWarmingAsync(List<UserDto> users) {
        List<CompletableFuture<Void>> futures = users.stream()
                .map(user -> CompletableFuture.runAsync(
                        () -> warmUpCacheForUser(user.id()), taskExecutor))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void warmUpCacheForUser(Long userId) {
        log.debug("Warming cache for user {}", userId);

        try {
            List<Long> followedAuthorIds = getFollowedAuthorIds(userId);
            if (followedAuthorIds.isEmpty()) {
                log.debug("User {} has no subscriptions. No feed to warm.", userId);
                return;
            }

            List<Post> recentPosts = getRecentPosts(followedAuthorIds);
            if (recentPosts.isEmpty()) {
                log.debug("No recent posts found for user {} from their subscriptions.", userId);
                return;
            }

            cachePosts(userId, recentPosts);

            log.info("Warmed cache for user {}. Added {} posts to feed.", userId, recentPosts.size());
        } catch (Exception e) {
            log.error("Failed to warm cache for user {}: {}", userId, e.getMessage(), e);
        }
    }

    private List<Long> getFollowedAuthorIds(Long userId) {
        List<UserDto> followedAuthors = subscriptionClient.getFollowersByUserId(userId);
        return followedAuthors == null
                ? Collections.emptyList()
                : followedAuthors.stream()
                .map(UserDto::id)
                .toList();
    }

    private List<Post> getRecentPosts(List<Long> authorIds) {
        return postRepository.findRecentPublishedPostsByAuthorIds(
                authorIds,
                feedProperties.getMaxFeedSize()
        );
    }

    private void cachePosts(Long userId, List<Post> posts) {
        for (Post post : posts) {
            if (post.getPublishedAt() == null) {
                log.warn("Post with ID {} for user {} feed warming has null publishedAt, skipping. " +
                        "Query in PostRepository might need adjustment.", post.getId(), userId);
                continue;
            }

            long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            redisCacheService.addToFeed(userId, new KafkaTimePostIdEvent(post.getId(), timestamp));
        }
    }
}
