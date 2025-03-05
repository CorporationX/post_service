package faang.school.postservice.service.cash;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.redis.entities.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.feed.UserFeedZSetService;
import faang.school.postservice.service.subscription.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CacheWarmerService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserFeedZSetService userFeedZSetService;
    private final UserContext userContext;
    private final SubscriptionService subscriptionService;
    private final CommentCacheService commentCacheService;
    private final PostMapper postMapper;
    private final ExecutorService executor;

    @Value("${spring.data.cache.warmup.batch-size}")
    private int batchSize;

    public CacheWarmerService(
            PostRepository postRepository,
            UserServiceClient userServiceClient,
            PostCacheRepository postCacheRepository,
            UserCacheRepository userCacheRepository,
            UserFeedZSetService userFeedZSetService,
            UserContext userContext,
            SubscriptionService subscriptionService,
            CommentCacheService commentCacheService,
            PostMapper postMapper,

            @Qualifier("cacheWarmerExecutor") ExecutorService executor) {
        this.postRepository = postRepository;
        this.userServiceClient = userServiceClient;
        this.postCacheRepository = postCacheRepository;
        this.userCacheRepository = userCacheRepository;
        this.userFeedZSetService = userFeedZSetService;
        this.userContext = userContext;
        this.subscriptionService = subscriptionService;
        this.commentCacheService = commentCacheService;
        this.postMapper = postMapper;
        this.executor = executor;
    }

    @Async("taskExecutor")
    public void warmUpCache() {
        log.info("Starting cache warm-up process");
        try {
            long currentUserId = userContext.getUserId();
            Set<Long> activeAuthors = subscriptionService.getAuthorIds();
            Set<Long> activeProjects = subscriptionService.getProjectIds();

            List<CompletableFuture<Void>> tasks = createWarmUpTasks(currentUserId, activeAuthors, activeProjects);

            waitForTasksCompletion(tasks);
            log.info("Cache warm-up completed successfully");
        } catch (Exception e) {
            log.error("Error during cache warm-up", e);
            throw new RuntimeException("Cache warm-up failed", e);
        }
    }

    private List<CompletableFuture<Void>> createWarmUpTasks(long currentUserId,
                                                            Set<Long> activeAuthors,
                                                            Set<Long> activeProjects) {
        CompletableFuture<Void> usersCaching = createUsersCachingTask(currentUserId, activeAuthors);
        CompletableFuture<Void> postsCaching = createPostsCachingTask(currentUserId, activeAuthors);
        CompletableFuture<Void> feedsCaching = createFeedsCachingTask(currentUserId, activeAuthors, activeProjects);

        return List.of(usersCaching, postsCaching, feedsCaching);
    }

    protected CompletableFuture<Void> createUsersCachingTask(long currentUserId, Set<Long> activeAuthors) {
        return CompletableFuture.runAsync(() -> {
            executeWithUserContext(currentUserId, () -> warmUpUsers(activeAuthors));
        }, executor);
    }

    protected CompletableFuture<Void> createPostsCachingTask(long currentUserId, Set<Long> activeAuthors) {
        return CompletableFuture.runAsync(() -> {
            executeWithUserContext(currentUserId, () -> warmUpPosts(activeAuthors));
        }, executor);
    }

    protected CompletableFuture<Void> createFeedsCachingTask(long currentUserId,
                                                             Set<Long> activeAuthors,
                                                             Set<Long> activeProjects) {
        return CompletableFuture.runAsync(() -> {
            executeWithUserContext(currentUserId, () -> warmUpFeeds(activeAuthors, activeProjects));
        }, executor);
    }

    private void executeWithUserContext(long userId, Runnable task) {
        userContext.setUserId(userId);
        try {
            task.run();
        } finally {
            userContext.clear();
        }
    }

    private void waitForTasksCompletion(List<CompletableFuture<Void>> tasks) {
        try {
            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Cache warm-up interrupted", e);
        } catch (Exception e) {
            throw new RuntimeException("Error waiting for cache warm-up tasks", e);
        }
    }

    private void warmUpUsers(Set<Long> userIds) {
        try {
            int totalUsers = userIds.size();
            int processedUsers = 0;

            for (List<Long> batch : Lists.partition(new ArrayList<>(userIds), batchSize)) {
                for (Long userId : batch) {
                    try {
                        userServiceClient.findById(userId).ifPresent(user -> {
                            UserCache userCache = createUserCache(user);
                            userCacheRepository.save(userCache);
                        });
                    } catch (Exception e) {
                        log.error("Error caching user {}", userId, e);
                    }
                }
                processedUsers += batch.size();
                log.info("Cached {} users out of {}", processedUsers, totalUsers);
            }
        } catch (Exception e) {
            log.error("Error in warmUpUsers", e);
            throw e;
        }
    }

    private void warmUpPosts(Set<Long> authorIds) {
        try {
            AtomicInteger totalProcessed = new AtomicInteger(0);

            for (Long userId : authorIds) {
                try {
                    List<Post> posts = postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId);
                    for (Post post : posts) {
                        if (post.canBeAddedToFeed()) {
                            try {
                                PostCache postCache = postMapper.toPostCache(post);
                                postCache.setLastComments(commentCacheService.fetchLatestComments(post.getId()));
                                postCacheRepository.save(postCache);
                                totalProcessed.incrementAndGet();
                            } catch (Exception e) {
                                log.error("Error caching post {} for user {}", post.getId(), userId, e);
                            }
                        }
                    }
                    log.info("Processed {} posts for user {}, total posts processed: {}",
                            posts.size(), userId, totalProcessed.get());
                } catch (Exception e) {
                    log.error("Error processing posts for user {}", userId, e);
                }
            }
            log.info("Finished caching posts. Total posts processed: {}", totalProcessed.get());
        } catch (Exception e) {
            log.error("Error in warmUpPosts", e);
            throw e;
        }
    }

    private void warmUpFeeds(Set<Long> activeAuthorIds, Set<Long> projectIds) {
        try {
            AtomicInteger totalProcessedSubscribers = new AtomicInteger(0);
            processUserFeeds(activeAuthorIds, totalProcessedSubscribers);
            processProjectFeeds(projectIds, totalProcessedSubscribers);

            log.info("Completed all feed warm-ups. Total subscribers processed: {}", totalProcessedSubscribers.get());
        } catch (Exception e) {
            log.error("Error during feed warm-up process", e);
            throw e;
        }
    }

    private void processUserFeeds(Set<Long> activeAuthorIds, AtomicInteger totalProcessed) {
        for (Long authorId : activeAuthorIds) {
            try {
                List<Post> authorPosts = postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(authorId);
                if (authorPosts.isEmpty()) {
                    continue;
                }

                List<Post> validPosts = authorPosts.stream()
                        .filter(Post::canBeAddedToFeed)
                        .collect(Collectors.toList());

                if (validPosts.isEmpty()) {
                    continue;
                }

                List<Long> subscriberIds = userServiceClient.getUserSubscribersIds(authorId);
                log.info("Found {} subscribers for author {}", subscriberIds.size(), authorId);

                updateSubscriberFeeds(subscriberIds, validPosts, totalProcessed, "author", authorId);
            } catch (Exception e) {
                log.error("Failed to process author {} feeds", authorId, e);
            }
        }
    }

    private void processProjectFeeds(Set<Long> projectIds, AtomicInteger totalProcessed) {
        for (Long projectId : projectIds) {
            try {
                List<Post> projectPosts = postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId);
                if (projectPosts.isEmpty()) {
                    continue;
                }

                List<Post> validPosts = projectPosts.stream()
                        .filter(Post::canBeAddedToFeed)
                        .collect(Collectors.toList());

                if (validPosts.isEmpty()) {
                    continue;
                }

                List<Long> subscriberIds = userServiceClient.getProjectSubscriptions(projectId);
                log.info("Found {} subscribers for project {}", subscriberIds.size(), projectId);

                updateSubscriberFeeds(subscriberIds, validPosts, totalProcessed, "project", projectId);
            } catch (Exception e) {
                log.error("Failed to process project {} feeds", projectId, e);
            }
        }
    }

    private void updateSubscriberFeeds(
            List<Long> subscriberIds,
            List<Post> posts,
            AtomicInteger totalProcessed,
            String sourceType,
            Long sourceId) {

        for (List<Long> batch : Lists.partition(subscriberIds, batchSize)) {
            for (Long subscriberId : batch) {
                try {
                    for (Post post : posts) {
                        userFeedZSetService.addPostToFeed(
                                subscriberId,
                                post.getId(),
                                post.getCreatedAt()
                        );
                    }
                    totalProcessed.incrementAndGet();
                } catch (Exception e) {
                    log.error("Failed to update feed for subscriber {} of {} {}",
                            subscriberId, sourceType, sourceId, e);
                }
            }
            log.info("Processed batch of {} subscribers for {} {}, total processed: {}",
                    batch.size(), sourceType, sourceId, totalProcessed.get());
        }
    }

    private UserCache createUserCache(UserDto user) {
        return UserCache.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    private CommentCache createCommentCache(Comment comment) {
        return CommentCache.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}