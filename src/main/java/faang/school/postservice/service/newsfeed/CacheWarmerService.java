package faang.school.postservice.service.newsfeed;

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
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheWarmerService {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final RedisCacheService redisCacheService;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final FeedProperties feedProperties;

    @Async
    public void warmUpCache() {
        log.info("Cache warming process started.");
        List<UserDto> allUsersToProcess = new ArrayList<>();
        int page = 0;
        int pageSize = feedProperties.getCacheWarmerUserBatchSize();
        List<UserDto> userBatch;
        do {
            log.info("Fetching users page: {}, size: {}", page, pageSize);
            try {
                userBatch = userServiceClient.getUsersByPage(page, pageSize);
                if (userBatch != null && !userBatch.isEmpty()) {
                    allUsersToProcess.addAll(userBatch);
                    page++;
                } else {
                    log.info("No more users found or empty batch received at page {}.", page);
                    break;
                }
            } catch (Exception e) {
                log.error("Error fetching users at page {}: {}", page, e.getMessage(), e);
                break;
            }
        } while (userBatch.size() == pageSize);

        if (allUsersToProcess.isEmpty()) {
            log.info("No users found to warm cache for. Cache warming process finished.");
            return;
        }

        log.info("Starting cache warming for {} users in total.", allUsersToProcess.size());
        List<CompletableFuture<Void>> futures = allUsersToProcess.stream()
                .map(userDto -> CompletableFuture.runAsync(
                        () -> warmUpCacheForUser(userDto.id()), taskExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("Cache warming completed for all processed users."))
                .exceptionally(ex -> {
                    log.error("Error during cache warming completion: ", ex);
                    return null;
                });
    }

    public void warmUpCacheForUser(Long userId) {
        log.debug("Warming cache for user {}", userId);
        try {
            List<UserDto> followedAuthors = userServiceClient.getFollowersByUserId(userId);

            if (followedAuthors == null || followedAuthors.isEmpty()) {
                log.debug("User {} has no subscriptions. No feed to warm.", userId);
                return;
            }

            List<Long> followedAuthorIds = followedAuthors.stream()
                    .map(UserDto::id)
                    .toList();

            List<Post> recentPosts = postRepository.findRecentPublishedPostsByAuthorIds(
                    followedAuthorIds,
                    feedProperties.getMaxFeedSize()
            );

            if (recentPosts.isEmpty()) {
                log.debug("No recent posts found for user {} from their subscriptions.", userId);
                return;
            }

            recentPosts.forEach(post -> {
                if (post.getPublishedAt() != null) {
                    long publishedAtTimestamp = post.getPublishedAt()
                            .toInstant(ZoneOffset.UTC)
                            .toEpochMilli();
                    redisCacheService.addToFeed(userId, new KafkaTimePostIdEvent(post.getId(), publishedAtTimestamp));
                } else {
                    log.warn("Post with ID {} for user {} feed warming has null publishedAt, skipping. " +
                            "Query in PostRepository might need adjustment.", post.getId(), userId);
                }
            });

            log.info("Warmed cache for user {}. Added {} posts to feed.", userId, recentPosts.size());
        } catch (Exception e) {
            log.error("Failed to warm cache for user {}: {}", userId, e.getMessage(), e);
        }
    }
}
