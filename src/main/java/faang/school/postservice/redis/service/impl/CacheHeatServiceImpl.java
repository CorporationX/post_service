package faang.school.postservice.redis.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.service.AuthorCacheService;
import faang.school.postservice.redis.service.CacheHeatService;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheHeatServiceImpl implements CacheHeatService {

    @Value("${cache.heat.process-batch-size}")
    private int batchSize;

    @Value("${cache.heat.max-posts}")
    private int maxSize;

    @Value("${cache.heat.max-days-back}")
    private int maxDaysBack;

    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final AuthorCacheService authorCacheService;
    private final PostRepository postRepository;
    private final PostCacheService postCacheService;
    private final FeedCacheService feedCacheService;

    @Async
    public void heatCache(long startUserId, long endUserId) {
        List<UserDto> usersRange = userServiceClient.getUsersByIdRange(startUserId, endUserId);
        log.info("Total users fetched: {}", usersRange.size());

        List<List<UserDto>> userBatches = partitionList(usersRange, batchSize);
        log.info("Users divided into {} batches.", userBatches.size());

        userBatches.forEach(this::processBatch);
    }

    private void processBatch(List<UserDto> batch) {
        log.info("Processing batch with {} users.", batch.size());

        for (UserDto follower : batch) {

            List<Long> followeeIds = follower.getFollowees();
            if (followeeIds == null || followeeIds.isEmpty()) {
                log.info("User {} has no followees. Skipping.", follower.getId());
                continue;
            }

            List<UserDto> bloggers = userServiceClient.getUsersByIds(followeeIds);
            log.info("Fetched {} bloggers for user {}.", bloggers.size(), follower.getId());

            List<PostDto> allFeedPosts = getAllFeedPosts(bloggers);

            populateCache(follower.getId(), bloggers, allFeedPosts);
        }

        log.info("Batch processing completed.");
    }

    public void populateCache(Long followerId, List<UserDto> bloggers, List<PostDto> posts) {

        CompletableFuture<Void> saveAuthorsFuture = authorCacheService.saveAllAuthorsToCache(bloggers);
        saveAuthorsFuture.thenRun(() -> log.info("Authors have been successfully saved to cache.")).exceptionally(ex -> {
            log.error("Error saving authors to cache", ex);
            return null;
        });

        CompletableFuture<Void> savePostsFuture = postCacheService.saveAllPostsToCache(posts);
        savePostsFuture.thenRun(() -> log.info("Posts have been successfully saved to cache.")).exceptionally(ex -> {
            log.error("Error saving posts to cache", ex);
            return null;
        });

        LinkedList<Long> postIds = posts.stream()
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .map(PostDto::getId)
                .collect(Collectors.toCollection(LinkedList::new));

        FeedCache feedCache = FeedCache.builder()
                .id(followerId)
                .postIds(postIds)
                .build();

        feedCacheService.savePreparedFeed(feedCache);
    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    private List<PostDto> getAllFeedPosts(List<UserDto> bloggers) {
        List<PostDto> feedPosts = new ArrayList<>();
        int daysBack = 0;

        while (feedPosts.size() < maxSize && daysBack < maxDaysBack) {

            for (UserDto blogger : bloggers) {
                LocalDateTime endDate = LocalDateTime.now().minusDays(daysBack);
                LocalDateTime startDate = endDate.minusDays(1);

                List<PostDto> posts = postMapper.toPostDtos(
                        postRepository.getUserPublishedPostsByDateRange(blogger.getId(), startDate, endDate));

                if (!posts.isEmpty()) {
                    feedPosts.addAll(posts);
                }
            }

            daysBack++;
        }

        feedPosts = feedPosts.stream()
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .limit(maxSize)
                .toList();

        return feedPosts;
    }
}
