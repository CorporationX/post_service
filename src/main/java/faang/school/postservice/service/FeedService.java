package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    @Value("${spring.data.redis.util.comments-amount}")
    private int maxAmountOfComments;

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public RedisFeed saveFeed(RedisFeed redisFeed) {
        RedisFeed savedFeed = redisFeedRepository.save(redisFeed);
        log.info("User's feed with ID: {} has been successfully saved into a Redis", redisFeed.getUserId());
        return savedFeed;
    }

    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(1000))
    public RedisFeed updateFeed(long userId, RedisFeed redisFeed) {
        RedisFeed updatedFeed = keyValueTemplate.update(userId, redisFeed);
        log.info("User's feed with ID: {} has been successfully updated", userId);
        return updatedFeed;
    }

    public FeedDto getUserFeedBy(Long postId) {
        Long userId = userContext.getUserId();

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is missing");
        }

        return postId == null ? getFirstPostsFromFeed(userId) : getFeedAfterPostWithId(postId, userId);
    }

    @Async("feedTaskExecutor")
    public void saveSinglePostToFeed(long userId, PostPair postPair) {
        redisFeedRepository.findById(userId)
                .ifPresentOrElse(
                        redisFeed -> {
                            LinkedHashSet<PostPair> posts = redisFeed.getPosts();

                            log.info("Redis Feed exist in Redis, User with ID: {}, has {} amount of posts in feed", userId, posts.size());
                            List<PostPair> postPairs = new ArrayList<>(posts);

                            int amountOfPosts = postPairs.size();

                            if (amountOfPosts >= maxFeedInRedis) {
                                log.warn("List of posts in Redis are overfilled, cutting size from {}, to {}", amountOfPosts, maxFeedInRedis - 1);

                                postPairs.subList(maxFeedInRedis - 1, amountOfPosts).clear();
                            }
                            postPairs.add(0, postPair);
                            redisFeed.setPosts(new LinkedHashSet<>(postPairs));

                            updateFeed(userId, redisFeed);
                        },
                        () -> {
                            log.warn("Feed with User ID: {}, are not exist in Redis", userId);

                            RedisFeed newFeed = buildEmptyFeed(userId);
                            newFeed.getPosts().add(postPair);
                            log.info("New feed was created with size {}", newFeed.getPosts().size());

                            saveFeed(newFeed);
                        }
                );
    }

    public void deleteSinglePostInFeed(List<Long> userIds, long postId) {
        userIds.forEach(userId -> {
            redisFeedRepository.findById(userId)
                    .ifPresent(redisFeed -> {
                        log.info("Feed for User with ID: {} exist in Redis. Attempting to delete Post with ID: {} from user feed", userId, postId);

                        List<PostPair> postPairs = new ArrayList<>(redisFeed.getPosts());

                        postPairs.stream()
                                .filter(postPair -> postPair.postId() == postId)
                                .findFirst()
                                .ifPresent(postPair -> {

                                    redisFeed.getPosts().remove(postPair);
                                    updateFeed(userId, redisFeed);

                                });
                    });
        });
        log.info("Attempting to delete Post with ID: {} from Redis", postId);
        redisCacheService.deleteRedisPost(postId);
    }

    @Transactional
    public void updateSinglePostInRedis(long postId) {
        postService.findAlreadyPublishedAndNotDeletedPost(postId)
                .ifPresentOrElse(
                        post -> redisCacheService.findRedisPostBy(postId).ifPresentOrElse(
                                redisPost -> {
                                    log.info("Post with ID: {} has been successfully found in Redis", postId);
                                    redisCacheService.updatePost(redisPost, post);
                                },
                                () -> redisCacheService.cachePost(post)
                        ),
                        () -> {
                            throw new EntityNotFoundException(String.format("Post with ID: %d doesn't exist", postId));
                        }
                );
    }

    @Async("commentTaskExecutor")
    public void addCommentToPost(long postId, RedisCommentDto commentDto) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Post with ID {} were found in Redis", postId);

                            List<RedisCommentDto> comments = redisPost.getCommentsDto();

                            if (comments == null) {
                                log.warn("List of comments in Post with ID {} is empty. Creating a list and adding a single comment.", postId);

                                comments = new ArrayList<>(maxAmountOfComments);
                            } else {
                                int amountOfComments = comments.size();

                                if (amountOfComments >= maxAmountOfComments) {
                                    log.warn("There are too many comments in the Post with ID {}. Deleting the last one.", postId);

                                    comments.subList(maxAmountOfComments - 1, amountOfComments).clear();
                                }
                            }
                            comments.add(0, commentDto);
                            redisPost.setCommentsDto(comments);
                            redisPost.incrementPostVersion();

                            redisCacheService.updateRedisPost(postId, redisPost);
                            log.info("Comment with ID: {}, has been successfully added to Post with ID: {}. Amount of comments {}", commentDto.getId(), postId, comments.size());
                        },
                        () -> {
                            log.info("Post with ID: {} is not in Redis. Attempting to retrieve it from database", postId);
                            findAndCachePost(postId);
                        }
                );
    }

    public void updateCommentInPost(Long postId, RedisCommentDto updatedComment) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Post with ID: {} is present in Redis. Attempting to update comment with ID: {}", postId, updatedComment.getId());
                            List<RedisCommentDto> commentDtos = redisPost.getCommentsDto();

                            commentDtos.stream()
                                    .filter(comment -> comment.getId() == updatedComment.getId())
                                    .findFirst()
                                    .ifPresent(oldComment -> {
                                        log.info("Comment with ID: {} is present in Post. Attempting to update", postId);

                                        int index = commentDtos.indexOf(oldComment);
                                        commentDtos.set(index, updatedComment);

                                        redisPost.setCommentsDto(commentDtos);
                                        redisPost.incrementPostVersion();

                                        redisCacheService.updateRedisPost(postId, redisPost);
                                    });
                        },
                        () -> {
                            log.warn("Post with ID: {} was not found in Redis. Attempting to retrieve from the database and cache in Redis.", postId);
                            findAndCachePost(postId);
                        }
                );
    }

    public void deleteCommentFromPost(long postId, long commentId) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Post with ID: {} is present in Redis. Attempting to remove comment with ID: {}", postId, commentId);
                            List<RedisCommentDto> comments = redisPost.getCommentsDto();
                            boolean isRemoved = comments.removeIf(comment -> comment.getId() == commentId);

                            if (isRemoved) {
                                log.info("Comment with ID: {} has been removed successfully. Amount of comments {}", commentId, comments.size());

                                redisPost.incrementPostVersion();
                                redisCacheService.updateRedisPost(postId, redisPost);
                            } else {
                                log.info("Comment with ID: {} is not in RedisPost and does not need removal", commentId);
                            }
                        },
                        () -> {
                            log.info("Post with ID: {} is not in Redis. Attempting to retrieve it from database", postId);
                            findAndCachePost(postId);
                        });
    }

    @Async("likePostTaskExecutor")
    public void incrementOrDecrementPostLike(long postId, LikeAction likeAction) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Attempting to decrement post like to Post with ID: {}", postId);

                            if (likeAction.equals(LikeAction.ADD)) {
                                redisPost.incrementPostLike();
                            } else {
                                redisPost.decrementPostLike();
                            }

                            redisPost.incrementPostVersion();
                            redisCacheService.updateRedisPost(postId, redisPost);
                            log.info("Post with ID: {} has been successfully decremented his like and updated. Amount of likes {}", postId, redisPost.getPostLikes());
                        },
                        () -> {
                            log.warn("Post with ID {} not found in Redis, attempting to retrieve it from the Database and save it in Redis", postId);
                            findAndCachePost(postId);
                        }
                );
    }

    @Async("likeCommentTaskExecutor")
    public void incrementOrDecrementPostCommentLike(long postId, long commentId, LikeAction likeAction) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Comment with ID: {} is present in Post with ID {}. Attempting to add like", postId, commentId);

                            List<RedisCommentDto> comments = redisPost.getCommentsDto();

                            comments.stream()
                                    .filter(commentDto -> commentDto.getId() == commentId)
                                    .findFirst()
                                    .ifPresent(commentDto -> {

                                        if (likeAction.equals(LikeAction.ADD)) {
                                            commentDto.incrementCommentLikes();
                                        } else {
                                            commentDto.decrementCommentLikes();
                                        }

                                        int index = comments.indexOf(commentDto);
                                        comments.set(index, commentDto);

                                        redisPost.setCommentsDto(comments);
                                        redisPost.incrementPostVersion();

                                        redisCacheService.updateRedisPost(postId, redisPost);
                                        log.info("Comment with ID: {} has been successfully incremented his like and updated. Amount of likes {}", postId, commentDto.getAmountOfLikes());
                                    });
                        },
                        () -> {
                            log.warn("Post with ID {} not found in Redis, attempting to retrieve it from the Database and save it in Redis", postId);
                            findAndCachePost(postId);
                        }
                );
    }

    @Async("postViewsTaskExecutor")
    public void incrementPostView(long postId) {
        redisCacheService.findRedisPostBy(postId)
                .ifPresentOrElse(
                        redisPost -> {
                            log.info("Post with ID: {} exist in Redis. Amount of Post views {}", postId, redisPost.getPostViews());

                            redisPost.incrementPostView();
                            redisPost.incrementPostVersion();

                            redisCacheService.updateRedisPost(postId, redisPost);
                        },
                        () -> {
                            log.warn("Post with ID {} doesn't exist in Redis. Attempting to retrieve it from the database and increment view.", postId);

                            postService.incrementPostViewByPostId(postId);
                            findAndCachePost(postId);
                        }
                );
    }

    public List<RedisPost> mapAndUpdateOrCachePostInRedis(List<Post> posts) {
        return posts.stream()
                .map(redisCacheService::updateOrCachePost)
                .collect(Collectors.toList());
    }

    @Transactional
    private FeedDto getFirstPostsFromFeed(Long userId) {
        log.info("Trying to get first {} posts for User with ID: {}", responseFeedSize, userId);

        Optional<RedisFeed> optionalRedisFeed = redisFeedRepository.findById(userId);

        if (optionalRedisFeed.isPresent()) {
            LinkedHashSet<PostPair> postPairs = optionalRedisFeed.get().getPosts();

            log.info("Redis Feed exist in Redis, User with ID: {}, has {} amount of posts in feed", userId, postPairs.size());
            List<Long> postIds = postPairs.stream()
                    .map(PostPair::postId)
                    .collect(Collectors.toList());

            List<RedisPost> redisPosts = postService.findRedisPostsByAndCacheThemIfNotExist(postIds);
            log.info("Found {} posts for User with ID: {}", redisPosts.size(), userId);

            int amountOfPosts = redisPosts.size();

            if (amountOfPosts < responseFeedSize) {
                log.warn("Current amount of posts in the feed are less then needed. Current amount: {}. Required amount: {}.", amountOfPosts, responseFeedSize);

                List<RedisPost> missingPosts = findMissingPostsAndCacheThem(postIds, userId, amountOfPosts);
                redisPosts.addAll(missingPosts);

                log.info("Added missing posts to user feed. Number of missing posts: {}", missingPosts.size());
            }
            List<Long> postViewIds = redisPosts.stream()
                    .map(RedisPost::getPostId)
                    .toList();

            postService.publishPostViewEventToKafka(postViewIds);

            redisPosts.sort(Comparator.comparing(RedisPost::getPublishedAt).reversed());

            List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts);
            return buildFeedDto(userId, feedData);
        }
        log.warn("No user feed found in Redis for User with ID: {}. Attempting to build a new feed.", userId);

        UserDto userDto = redisCacheService.findUserBy(userId);
        redisCacheService.updateOrCacheUser(userDto);

        List<Long> followeeIds = userDto.getFolloweeIds();

        if (followeeIds == null || followeeIds.isEmpty()) {
            log.warn("User with ID: {} has no followers, attempting to build an empty News Feed", userId);

            saveFeed(buildEmptyFeed(userId));

            return buildEmptyFeedDto(userId);
        }
        List<Post> sortedPosts = postService.findSortedPostsByAuthorIdsLimit(followeeIds, maxFeedInRedis);
        log.info("Found {} posts for the new User feed with ID: {}", sortedPosts.size(), userId);

        List<RedisPost> redisPosts = mapAndUpdateOrCachePostInRedis(sortedPosts);
        List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts);

        buildAndSaveFeed(userId, redisPosts);

        return buildFeedDto(userId, feedData);
    }


    @Transactional
    private FeedDto getFeedAfterPostWithId(Long postId, Long userId) {
        log.info("Trying to get feed after Post with ID: {}, for User with ID: {}", postId, userId);

        Optional<RedisFeed> optionalFeed = redisFeedRepository.findById(userId);
        if (optionalFeed.isPresent()) {
            List<Long> postIds = optionalFeed.get().getPosts().stream()
                    .map(PostPair::postId)
                    .toList();

            int amountOfPostsInRedis = postIds.size();
            int maxRequiredAmount = maxFeedInRedis - amountOfPostsInRedis;
            log.info("Redis Feed exist in Redis, User with ID: {}, has {} amount of posts in feed", userId, amountOfPostsInRedis);

            int indexOfPost = postIds.indexOf(postId);

            if (indexOfPost >= 0) {
                log.info("Post with ID: {} has been found in the Feed, at index {}", postId, indexOfPost);
                int indexOfLastElement = amountOfPostsInRedis - 1;

                if (indexOfPost == indexOfLastElement) {
                    log.info("Post with ID: {} is the last post in the feed. Attempting to construct the feed from the database.", postId);

                    return buildFeedFromDatabaseFromPostBy(postId, userId, maxRequiredAmount);
                }
                int startIndex = indexOfPost + 1;
                int endIndex = Math.min(startIndex + responseFeedSize, amountOfPostsInRedis);

                List<Long> nextTwentyPostsIds = postIds.subList(startIndex, endIndex); //1
                log.info("Found {} posts starting from the post with ID: {}", nextTwentyPostsIds.size(), postId);

                List<RedisPost> responseList = nextTwentyPostsIds.stream()
                        .map(postService::findRedisPostAndCacheHimIfNotExist)
                        .collect(Collectors.toList());

                int amountOfPosts = responseList.size();

                if (amountOfPosts < responseFeedSize) {
                    log.warn("Insufficient number of posts for the user's feed with ID: {}. Found {}, but {} are required", userId, amountOfPosts, responseFeedSize);
                    List<RedisPost> remainingPosts = findRemainingPosts(postIds, userId);
                    responseList.addAll(remainingPosts);
                }
                responseList.sort(Comparator.comparing(RedisPost::getPublishedAt).reversed());

                return buildFeedDto(userId, mapRedisPostToRedisPostDto(responseList));
            }
            log.warn("The index of the Post with ID: {} was not found in the Redis feed." +
                    " Attempting to find posts based on the user's subscriptions, starting from required Post", postId);

            return buildFeedFromDatabaseFromPostBy(postId, userId, maxRequiredAmount);
        }
        log.warn("Redis Feed doesn't exist in Redis for User with ID: {}. Attempting to build feed from database", userId);

        return buildFeedFromDatabaseFromPostBy(postId, userId, maxFeedInRedis);
    }

    @Transactional
    private List<RedisPost> findMissingPostsAndCacheThem(List<Long> usedPostIds, long userId, int currentPostsForFeed) {
        int requiredResponseAmount = responseFeedSize - currentPostsForFeed;
        int requiredAmountInFeed = maxFeedInRedis - currentPostsForFeed;

        log.info("Trying to find missing Posts. Required amount: {}", requiredResponseAmount);
        UserDto userDto = redisCacheService.findUserBy(userId);
        redisCacheService.updateOrCacheUser(userDto);

        List<Long> followees = userDto.getFolloweeIds();

        List<Post> posts = postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followees, usedPostIds, requiredAmountInFeed);

        log.info("Found {} posts from User followees. Required: {}.", posts.size(), requiredAmountInFeed);
        posts.forEach(redisCacheService::updateOrCachePost);

        return posts.stream()
                .limit(requiredAmountInFeed)
                .map(redisCacheService::mapPostToRedisPostAndSetDefaultVersion)
                .collect(Collectors.toList());
    }

    private FeedDto buildFeedFromDatabaseFromPostBy(Long postId, Long userId, int requiredAmount) {
        UserDto userDto = redisCacheService.findUserBy(userId);
        redisCacheService.updateOrCacheUser(userDto);

        List<Long> followees = userDto.getFolloweeIds();

        if (followees == null || followees.isEmpty()) {
            log.warn("User with ID: {} has no followers. Attempting to create an empty feed.", userId);

            saveFeed(buildEmptyFeed(userId));

            return buildEmptyFeedDto(userId);
        }
        LocalDateTime lastWatchedPostDate = postService.findRedisPostAndCacheHimIfNotExist(postId)
                .getPublishedAt();

        List<Post> sortedPosts = postService.findSortedPostsFromPostDateAndAuthorsLimit(followees, lastWatchedPostDate, requiredAmount);

        log.info("Found {} posts for the User feed with ID: {}", sortedPosts.size(), userId);
        List<Long> postViewIds = sortedPosts.stream()
                .map(Post::getId)
                .toList();

        postService.publishPostViewEventToKafka(postViewIds);

        List<RedisPost> redisPosts = mapAndUpdateOrCachePostInRedis(sortedPosts);

        buildAndSaveFeed(userId, redisPosts);

        List<RedisPostDto> feedData = mapRedisPostToRedisPostDto(redisPosts.stream()
                .limit(responseFeedSize)
                .collect(Collectors.toList()));

        return buildFeedDto(userId, feedData);
    }

    private List<RedisPost> findRemainingPosts(List<Long> usedPostIds, long userId) {
        int requiredAmount = responseFeedSize - usedPostIds.size();

        log.info("Attempting to find the remaining posts for User with ID: {}. Required amount {}", userId, requiredAmount);
        List<Long> followeeIds = redisCacheService.findOrCacheRedisUser(userId)
                .getFolloweeIds();

        List<Post> posts = postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, usedPostIds, requiredAmount);

        log.info("Found {} posts based on user's subscriptions", posts.size());
        List<Long> postViewIds = posts.stream()
                .map(Post::getId)
                .toList();

        postService.publishPostViewEventToKafka(postViewIds);

        return mapAndUpdateOrCachePostInRedis(posts);
    }

    private void findAndCachePost(long postId) {
        postService.findAlreadyPublishedAndNotDeletedPost(postId)
                .ifPresentOrElse(
                        redisCacheService::cachePost,
                        () -> {
                            throw new EntityNotFoundException(String.format("Post with ID: %d already delete or not published yet", postId));
                        }
                );
    }

    private RedisFeed buildEmptyFeed(long userId) {
        return RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>())
                .build();
    }


    private RedisFeed buildFeed(long userId, LinkedHashSet<PostPair> postPairs) {
        return RedisFeed.builder()
                .userId(userId)
                .posts(postPairs)
                .build();
    }

    private void buildAndSaveFeed(long userId, List<RedisPost> redisPosts) {
        LinkedHashSet<PostPair> postPairs = redisPosts.stream()
                .map(redisPostMapper::toPostPair)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        saveFeed(buildFeed(userId, postPairs));
    }

    private FeedDto buildFeedDto(long userId, List<RedisPostDto> postsDto) {
        return FeedDto.builder()
                .requesterId(userId)
                .dtos(postsDto)
                .build();
    }

    private FeedDto buildEmptyFeedDto(long userId) {
        return FeedDto.builder()
                .requesterId(userId)
                .dtos(Collections.emptyList())
                .build();
    }

    private List<RedisPostDto> mapRedisPostToRedisPostDto(List<RedisPost> posts) {
        return posts.stream()
                .map(redisPost -> {
                    RedisUser redisUser = redisCacheService.findOrCacheRedisUser(redisPost.getAuthorId());
                    RedisUserDto dto = redisUserMapper.toDto(redisUser);

                    RedisPostDto redisPostDto = redisPostMapper.toRedisPostDto(redisPost);
                    redisPostDto.setUserDto(dto);

                    return redisPostDto;
                })
                .collect(Collectors.toList());
    }
}