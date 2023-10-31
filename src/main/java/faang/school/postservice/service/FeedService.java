package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostService postService;
    private final RedisCacheService redisCacheService;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;
    private final UserContext userContext;

    private final RedisKeyValueTemplate keyValueTemplate;

    @Value("${spring.data.redis.util.response-size}")
    private int responseFeedSize;
    @Value("${spring.data.redis.util.cache-size}")
    private int maxFeedInRedis;

    public void saveSinglePostToFeed(long userId, PostPair postPair) {
        Optional<RedisFeed> redisFeed = redisFeedRepository.findById(userId);

        if (redisFeed.isPresent()) {
            RedisFeed feed = redisFeed.get();
            LinkedHashSet<PostPair> posts = feed.getPosts();

            log.info("Redis Feed exist in Redis, User with ID: {}, has {} amount of posts in feed", userId, posts.size());
            List<PostPair> postPairs = new ArrayList<>(posts);

            int amountOfPosts = postPairs.size();

            if (amountOfPosts >= maxFeedInRedis) {
                log.warn("List of posts in Redis are overfilled, cutting size from {}, to {}", amountOfPosts, maxFeedInRedis - 1);
                postPairs.subList(maxFeedInRedis - 1, amountOfPosts).clear();
            }
            postPairs.add(0, postPair);
            feed.setPosts(new LinkedHashSet<>(postPairs));

            updateFeed(userId, feed);
        }
        log.info("Feed with User ID: {}, are not exist in Redis", userId);

        RedisFeed newFeed = buildEmptyFeed(userId);
        newFeed.getPosts().add(postPair);

        log.info("New feed was created with size {}", newFeed.getPosts().size());
        saveFeed(newFeed);
    }

    private RedisFeed buildEmptyFeed(long userId) {
        return RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>())
                .build();
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(1000))
    private RedisFeed saveFeed(RedisFeed redisFeed) {
        return redisFeedRepository.save(redisFeed);
    }

    private RedisFeed updateFeed(long userId, RedisFeed redisFeed) {
        return keyValueTemplate.update(userId, redisFeed);
    }

    public FeedDto getUserFeedBy(Long postId) {
        long userId = userContext.getUserId();

        return postId == null ? getFirstPostsFromFeed(userId) : getFeedAfterPostWithId(postId, userId);
    }

    @Transactional
    private FeedDto getFirstPostsFromFeed(long userId) {
        log.info("Trying to get first {} posts for User with ID: {}", responseFeedSize, userId);

        Optional<RedisFeed> optionalRedisFeed = redisFeedRepository.findById(userId);

        if (optionalRedisFeed.isPresent()) {
            LinkedHashSet<PostPair> postPairs = optionalRedisFeed.get().getPosts();

            log.info("Feed for User with ID: {} was found. Number of Posts in Feed: {}.", userId, postPairs.size());
            List<Long> postIds = postPairs.stream()
                    .map(PostPair::postId)
                    .collect(Collectors.toList());

            List<RedisPost> redisPosts = postService.findRedisPostsByAndCacheThemIfNotExist(postIds);

            int amountOfRedisPosts = redisPosts.size();

            if (amountOfRedisPosts < responseFeedSize) {
                log.warn("Current amount of posts in the feed are less then needed. Current amount: {}. Required amount: {}.", amountOfRedisPosts, responseFeedSize);

                List<RedisPost> missingPosts = findMissingPostsAndCacheThem(postIds, userId, amountOfRedisPosts);
                redisPosts.addAll(missingPosts);

                log.info("Added missing posts to user feed. Number of missing posts: {}", missingPosts.size());
            }
            redisPosts.sort(Comparator.comparing(RedisPost::getPublishedAt).reversed());

            redisPosts.stream()
                    .map(RedisPost::getPostId)
                    .forEach(postService::publishPostViewEventToKafka);

            List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts);
            return buildFeedDto(userId, feedData);
        }
        log.warn("No user feed found in Redis for User with ID: {}. Attempting to build a new feed.", userId);

        RedisUser user = redisCacheService.findOrCacheRedisUser(userId);
        List<Long> followeeIds = user.getFolloweeIds();

        if (followeeIds == null || followeeIds.isEmpty()) {
            return buildEmptyFeedDto(userId);
        }
        List<Post> sortedPosts = postService.findSortedPostsByAuthorIdsLimit(followeeIds, maxFeedInRedis);

        List<RedisPost> redisPosts = sortedPosts.stream()
                .map(post -> {
                    long postId = post.getId();
                    postService.incrementPostView(postId);

                    Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(postId);

                    if (optionalRedisPost.isPresent()) {
                        RedisPost redisPost = optionalRedisPost.get();
                        redisPost.incrementPostView();
                        redisPost.incrementPostVersion();

                        return redisCacheService.updateRedisPost(postId, redisPost);
                    }
                    return cachePost(post);
                })
                .collect(Collectors.toList());

        List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts);

        return buildFeedDto(userId, feedData);
    }

    @Transactional
    private List<RedisPost> findMissingPostsAndCacheThem(List<Long> usedPostIds, long userId, int currentPostsForFeed) {
        int requiredResponseAmount = responseFeedSize - currentPostsForFeed;
        int requiredAmountInFeed = maxFeedInRedis - currentPostsForFeed;

        log.info("Trying to find missing Posts. Required amount: {}", requiredResponseAmount);
        List<Long> followees = redisCacheService.findOrCacheRedisUser(userId)
                .getFolloweeIds();

        List<Post> posts = postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followees, usedPostIds, requiredAmountInFeed);

        log.info("Found {} posts from user followees. Required: {}.", posts.size(), requiredAmountInFeed);

        List<Post> responseList = posts.subList(0, requiredResponseAmount);

        posts.forEach(post -> {
            long postId = post.getId();
            if (!redisPostRepository.existsById(postId)) {
                cachePost(post);
            }
            postService.publishPostViewEventToKafka(postId);
        });

        return responseList.stream()
                .map(this::mapPostToRedisPost)
                .collect(Collectors.toList());
    }

    private List<RedisPostDto> mapRedisPostToRedisPostDto(List<RedisPost> posts) {
        return posts.stream()
                .map(redisPost -> {
                    RedisUserDto dto = redisUserMapper.toDto(redisCacheService.findOrCacheRedisUser(redisPost.getAuthorId()));
                    RedisPostDto redisPostDto = redisPostMapper.toRedisPostDto(redisPost);
                    redisPostDto.setUserDto(dto);

                    return redisPostDto;
                }).toList();
    }

    private RedisPost mapPostToRedisPost(Post post) {
        RedisPost redisPost = redisPostMapper.toRedisPost(post);
        redisPost.setVersion(1);

        return redisPost;
    }

    private FeedDto buildFeedDto(long userId, List<RedisPostDto> postsDto) {
        log.info("Attempting to build feed DTO for User with ID: {}.", userId);
        return FeedDto.builder()
                .requesterId(userId)
                .dtos(postsDto)
                .build();
    }

    private FeedDto buildEmptyFeedDto(long userId) {
        log.info("Attempting to build empty feed DTO for User with ID: {}.", userId);
        return FeedDto.builder()
                .requesterId(userId)
                .dtos(Collections.emptyList())
                .build();
    }

    private FeedDto getFeedAfterPostWithId(Long postId, Long userId) {
        log.info("Trying to get feed after post with ID: {}, for User with ID: {}", postId, userId);

        Optional<RedisFeed> optionalFeed = redisFeedRepository.findById(userId);

        if (optionalFeed.isPresent()) {
            List<Long> postIds = optionalFeed.get().getPosts().stream()
                    .map(PostPair::postId)
                    .toList();

            int indexOfPost = postIds.indexOf(postId);

            if (indexOfPost >= 0) {
                int indexOfLastElement = postIds.size() - 1;

                if (indexOfPost == indexOfLastElement) {
                    int requiredAmount = maxFeedInRedis - postIds.size();
                    return buildFeedFromDatabaseFromPostBy(postId, userId, requiredAmount);
                }
                int startIndex = indexOfPost + 1;
                int endIndex = Math.min(startIndex + responseFeedSize, postIds.size());

                List<Long> nextTwentyPostsIds = postIds.subList(startIndex, endIndex); //1

                List<RedisPost> responseList = nextTwentyPostsIds.stream()
                        .map(postService::findRedisPostBy)
                        .collect(Collectors.toList()); //1

                int size = nextTwentyPostsIds.size();//1

                if (size < responseFeedSize) {
                    List<RedisPost> remainingPosts = findRemainingPosts(nextTwentyPostsIds, userId);
                    responseList.addAll(remainingPosts);
                }
                responseList.sort(Comparator.comparing(RedisPost::getPublishedAt).reversed());

                return buildFeedDto(userId, mapRedisPostToRedisPostDto(responseList));
            }
            int requiredAmountOfPosts = maxFeedInRedis = postIds.size();

            return buildFeedIfNoFeedOrPostIdNotInList(postId, userId, requiredAmountOfPosts);
        }
        return buildFeedIfNoFeedOrPostIdNotInList(postId, userId, maxFeedInRedis);
    }

    private FeedDto buildFeedIfNoFeedOrPostIdNotInList(long postId, long userId, int requiredAmountOfPosts) {
        List<Long> followeeIds = redisCacheService.findOrCacheRedisUser(userId)
                .getFolloweeIds();
        LocalDateTime lastWatchedPostDate = postService.findRedisPostBy(postId)
                .getPublishedAt();

        List<Post> posts = postService.findSortedPostsFromPostDateAndAuthorsLimit(followeeIds, lastWatchedPostDate, requiredAmountOfPosts);

        posts.stream()
                .map(Post::getId)
                .forEach(postService::publishPostViewEventToKafka);

        List<RedisPost> redisPosts = posts.stream()
                .map(post -> {
                    long id = post.getId();
                    return redisPostRepository.existsById(id) ? mapPostToRedisPost(post) : cachePost(post);
                }).toList();

        List<RedisPostDto> redisPostDtos = mapRedisPostToRedisPostDto(redisPosts);
        return buildFeedDto(userId, redisPostDtos);
    }

    private FeedDto buildFeedFromDatabaseFromPostBy(Long postId, Long userId, int requiredAmount) {
        List<Long> followees = redisCacheService.findOrCacheRedisUser(userId)
                .getFollowerIds();

        if (followees == null || followees.isEmpty()) {
            return buildEmptyFeedDto(userId);
        }
        LocalDateTime lastWatchedPostDate = postService.findRedisPostBy(postId)
                .getPublishedAt();

        List<Post> sortedPosts = postService.findSortedPostsFromPostDateAndAuthorsLimit(followees, lastWatchedPostDate, requiredAmount);

        sortedPosts.stream()
                .map(Post::getId)
                .forEach(postService::publishPostViewEventToKafka);

        List<RedisPost> redisPosts = sortedPosts.stream()
                .map(post -> {
                    long id = post.getId();
                    return redisPostRepository.existsById(id) ? mapPostToRedisPost(post) : cachePost(post);
                })
                .toList();

        List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts.stream()
                .limit(responseFeedSize)
                .collect(Collectors.toList()));

        return buildFeedDto(userId, feedData);
    }

    private List<RedisPost> findRemainingPosts(List<Long> usedPostIds, long userId) {
        int requiredAmount = responseFeedSize - usedPostIds.size(); //19

        List<Long> followeeIds = redisCacheService.findOrCacheRedisUser(userId)
                .getFolloweeIds();

        List<Post> posts = postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, usedPostIds, requiredAmount);

        posts.stream()
                .map(Post::getId)
                .forEach(postService::publishPostViewEventToKafka);

        return posts.stream()
                .map(post -> {
                    long id = post.getId();
                    return redisPostRepository.existsById(id) ? mapPostToRedisPost(post) : cachePost(post);
                }).toList();
    }

    private RedisFeed buildRedisFeed(long userId, List<PostPair> pairs) {
        log.info("Building feed for User with ID: {} in progress.", userId);
        return RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>(pairs))
                .build();
    }

    private RedisPost updatePost(RedisPost oldPost, Post newPost) {
        oldPost.incrementPostVersion();
        long postId = newPost.getId();

        RedisPost updatedPost = redisPostMapper.toRedisPost(newPost);
        updatedPost.setVersion(oldPost.getVersion());

        log.info("Post with ID: {}, was successfully updated in Redis", postId);

        return redisCacheService.updateRedisPost(postId, updatedPost);
    }

    private RedisPost cachePost(Post post) {
        RedisPost newPost = mapPostToRedisPost(post);

        log.info("New Post with ID: {} was successfully cached in Redis.", post.getId());
        return redisCacheService.saveRedisPost(newPost);
    }
}