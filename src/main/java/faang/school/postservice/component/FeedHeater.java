package faang.school.postservice.component;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.feed.FeedWarmupBatchEvent;
import faang.school.postservice.dto.feed.UserSubscriptionsEvent;
import faang.school.postservice.dto.redis.FeedRedisDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.exception.PageOverflowException;
import faang.school.postservice.exception.UserServiceConnectionException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.AuthorRequestEventPublisher;
import faang.school.postservice.publisher.FeedWarmupEventPublisher;
import faang.school.postservice.repository.FeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeater {

    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;

    private final UserServiceClient userClient;
    private final FeedWarmupEventPublisher feedWarmupEventPublisher;
    private final AuthorRequestEventPublisher authorRequestEventPublisher;
    private final PostRedisRepository postRedisRepository;
    private final FeedRedisRepository feedRedisRepository;
    private final PostService postService;
    private final PostMapper postMapper;
    private final CommentService commentService;

    @Value("${batch.users-per-page}")
    private int pageSize;

    @Value("${spring.data.redis.object-cache-options.posts-count}")
    private int maxPostsCount;

    @Value("${spring.data.redis.object-cache-options.comments-count}")
    private int maxCommentsCount;

    @Value("${batch.users-forming-feed}")
    private int usersBatchSize;

    @Value("${processors-setting.released-processors}")
    private int releasedProcessors;

    public long startHeatingFeed() {
        long usersCount = returnUsersCount();
        long pageCount = (usersCount + pageSize - 1) / pageSize;

        if (pageCount > Integer.MAX_VALUE) {
            throw new PageOverflowException("Too many pages: %d. ", pageCount +
                    "Please, the number of users per page in the setting");
        }

        for (int page = 0; page < (int) pageCount; page++) {
            FeedWarmupBatchEvent event = new FeedWarmupBatchEvent(page, pageSize);
            feedWarmupEventPublisher.publish(event);
        }
        return usersCount;
    }

    public void processUsersFeed(UserSubscriptionsEvent event) {
        Map<Long, List<Long>> userSubscriptions = event.userSubscriptions();

        List<Map<Long, List<Long>>> userBatches = new ArrayList<>();
        Map<Long, List<Long>> currentBatch = new HashMap<>();

        for (Map.Entry<Long, List<Long>> entry : userSubscriptions.entrySet()) {
            currentBatch.put(entry.getKey(), entry.getValue());
            if (currentBatch.size() == usersBatchSize) {
                userBatches.add(new HashMap<>(currentBatch));
                currentBatch.clear();
            }
        }
        if (!currentBatch.isEmpty()) {
            userBatches.add(new HashMap<>(currentBatch));
        }

        int usedProcessors = Runtime.getRuntime().availableProcessors() - releasedProcessors;
        ExecutorService executor = Executors.newFixedThreadPool(usedProcessors);

        log.info("Processing {} user batches with {} threads in heat feed task", userBatches.size(), usedProcessors);
        List<CompletableFuture<Void>> tasks = userBatches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> processBatch(batch), executor))
                .toList();

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
    }

    private void processBatch(Map<Long, List<Long>> batch) {
        long timeStartProcessing = System.currentTimeMillis();
        Map<Long, List<FeedRedisDto>> userFeeds = new HashMap<>();

        List<Pair<Long, Long>> userAuthorPairs = batch.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(author -> Pair.of(entry.getKey(), author)))
                .toList();

        Set<Long> authorIds = userAuthorPairs.stream()
                .map(Pair::getRight)
                .collect(Collectors.toSet());

        List<Post> posts = postService.getPostsByAuthorIds(authorIds);

        Map<Long, List<Post>> postsByAuthor = posts.stream()
                .collect(Collectors.groupingBy(Post::getAuthorId));

        for (Map.Entry<Long, List<Long>> entry : batch.entrySet()) {
            Long userId = entry.getKey();
            List<Long> authors = entry.getValue();

            List<FeedRedisDto> redisDtoList = authors.stream()
                    .flatMap(authorId -> postsByAuthor.getOrDefault(authorId, List.of()).stream())
                    .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                    .limit(maxPostsCount)
                    .map(post -> new FeedRedisDto(post.getId(), post.getPublishedAt()))
                    .toList();

            userFeeds.put(userId, redisDtoList);
        }

        List<PostResponseDto> responseDtoList = postMapper.toResponseDtoList(posts);
        List<PostRedisDto> redisDtoList = postMapper.toRedisDtoList(responseDtoList);
        redisDtoList.forEach(postRedisRepository::savePost);

        authorIds.forEach(authorRequestEventPublisher::publish);

        posts.forEach(post -> {
            Long postId = post.getId();
            commentService.sendCommentsEventByPostId(postId, maxCommentsCount);
        });

        responseDtoList.forEach(post -> {
            postRedisRepository.addPostLikes(post.getId(), post.getLikeCount());
            postRedisRepository.addPostViews(post.getId(), post.getViewCount());
        });

        userFeeds.forEach((userId, listRedisDto) -> {
            if (!listRedisDto.isEmpty()) {
                feedRedisRepository.addFeedForUser(userId, listRedisDto);
            }
        });

        log.debug("Batch processed: users = {}, authors = {}, posts = {}, duration = {} ms",
                batch.size(), authorIds.size(), posts.size(), System.currentTimeMillis() - timeStartProcessing);
    }

    @Retryable(
            retryFor = UserServiceConnectionException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    private long returnUsersCount() {
        try {
            return userClient.getUsersCount();
        } catch (FeignException e) {
            throw new UserServiceConnectionException("User server returned an error: " + e.getMessage());
        }
    }
}
