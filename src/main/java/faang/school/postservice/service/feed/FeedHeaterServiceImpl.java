package faang.school.postservice.service.feed;

import faang.school.postservice.client.FollowServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.FeedCacheProperties;
import faang.school.postservice.config.properties.FeedHeaterProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.cache.PostCachePort;
import faang.school.postservice.service.cache.UserCachePort;
import faang.school.postservice.service.cache.mapper.PostCacheMapper;
import faang.school.postservice.service.cache.model.UserCacheDto;
import faang.school.postservice.service.post.PostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {

    private final FollowServiceClient followServiceClient;
    private final UserServiceClient userServiceClient;
    private final PostQueryService postQueryService;
    private final FeedCachePort feedCachePort;
    private final PostCachePort postCachePort;
    private final UserCachePort userCachePort;
    private final FeedHeaterProperties heaterProps;
    private final FeedCacheProperties feedCacheProps;
    private final TaskExecutor feedHeaterExecutor;

    @Override
    public void heatAsync() {
        CompletableFuture.runAsync(this::heat, feedHeaterExecutor);
    }

    @Override
    public void heat() {
        log.info("Feed warmup started");
        List<Long> userIds = safeList(followServiceClient.getAllUserIds());
        if (userIds.isEmpty()) {
            log.info("No users to warm up.");
            return;
        }
        List<CompletableFuture<Void>> tasks = new ArrayList<>(userIds.size());
        for (Long userId : userIds) {
            tasks.add(CompletableFuture.runAsync(() -> warmUser(userId), feedHeaterExecutor));
        }
        tasks.forEach(CompletableFuture::join);
        log.info("Feed warmup finished for {} users", userIds.size());
    }

    private void warmUser(Long userId) {
        try {
            List<Long> followees = safeList(followServiceClient.getFolloweeIds(userId));
            if (followees.isEmpty()) {
                return;
            }
            List<Post> latestPosts = postQueryService.findLatestPublishedByAuthors(followees, heaterProps.getPerUserPostLimit());
            latestPosts.forEach(post -> {
                double score = post.getPublishedAt() == null
                        ? System.currentTimeMillis()
                        : post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
                feedCachePort.addToFeed(userId, post.getId(), score);
                postCachePort.put(PostCacheMapper.fromEntity(post));
                try {
                    var author = userServiceClient.getUser(post.getAuthorId());
                    if (author != null && author.id() != null) {
                        userCachePort.put(new UserCacheDto(author.id(), author.username(), author.email()));
                    }
                } catch (Exception e) {
                    log.debug("Skip caching author {}: {}", post.getAuthorId(), e.getMessage());
                }
            });
            feedCachePort.trimToMaxSize(userId, feedCacheProps.getMaxSize());
        } catch (Exception e) {
            log.warn("Warmup failed for user {}: {}", userId, e.getMessage());
        }
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
