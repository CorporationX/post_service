package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.redis.TimedPostId;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostMapper redisPostMapper;

    @Value("${post.feed.batch-size}")
    private Integer postsBatchSize;
    @Value("${post.feed.feed-size}")
    private Integer postsFeedSize;

    public List<FeedDto> getFeed(Long postId) {
        Long userId = userContext.getUserId();
        Optional<RedisFeed> redisFeed = redisFeedRepository.findById(userId);
        if (redisFeed.isEmpty()) {
            log.info("Posts from database was delivered to user {}", userId);
            return getPostsFromDB(userId, postId);
        }
        RedisFeed feed = redisFeed.get();

        List<Long> nextTwentyPostIds = getNextPosts(postId, feed);
        if (nextTwentyPostIds.isEmpty()) {
            log.info("Posts from database was delivered to user {}", userId);
            return getPostsFromDB(userId, postId);
        }

        List<FeedDto> resultFeed = new ArrayList<>();

        for (Long id : nextTwentyPostIds) {
            RedisPost redisPost = getOrSaveRedisPost(id);
            RedisUser redisUser = getOrSaveRedisUser(redisPost.getAuthorId());
            resultFeed.add(buildFeedDto(redisUser, redisPost));
        }
        log.info("News feed to user with id={} was taken from redis, first post in the feedBatch have id={}",
                feed.getUserId(), resultFeed.get(0).getPostId());
        return resultFeed;
    }

    public void heatFeed() {
        userServiceClient.getAllUsersWithKafka();
        log.info("Feed heating is started");
    }

    public void heatUserFeed(UserDto userDto) {
        List<Long> followeeIds = userDto.getFolloweeIds();
        List<PostDto> firstPostsForFeed = postService.getFirstPostsForFeed(followeeIds, postsFeedSize);
        firstPostsForFeed.forEach(postDto -> {
            if (!redisPostRepository.existsById(postDto.getId())) {
                redisPostRepository.save(redisPostMapper.toRedisPost(postDto));
            }
            getOrSaveRedisUser(postDto.getAuthorId());
        });
        if (redisFeedRepository.findById(userDto.getId()).isEmpty()) {
            List<TimedPostId> list = firstPostsForFeed.stream().map(postDto -> TimedPostId.builder()
                    .publishedAt(postDto.getPublishedAt())
                    .postId(postDto.getId())
                    .build()).toList();
            SortedSet<TimedPostId> feed = new TreeSet<>(list);
            RedisFeed redisFeed = RedisFeed.builder().postIds(feed).userId(userDto.getId()).build();
            redisFeedRepository.save(redisFeed);
        }
    }

    private List<Long> getNextPosts(Long postId, RedisFeed feed) {
        TreeSet<TimedPostId> currentFeedPostIds;
        if (postId == null) {
            currentFeedPostIds = (TreeSet<TimedPostId>) feed.getPostIds();
            Iterator<TimedPostId> iterator = currentFeedPostIds.descendingIterator();
            return getPostsList(iterator);
        }

        RedisPost redisPost = getOrSaveRedisPost(postId);
        TimedPostId prevPostId = TimedPostId.builder()
                .postId(postId)
                .publishedAt(redisPost.getPublishedAt())
                .build();

        if (feed.getPostIds().contains(prevPostId)) {
            currentFeedPostIds = (TreeSet<TimedPostId>) feed.getPostIds().headSet(prevPostId);
            Iterator<TimedPostId> iterator = currentFeedPostIds.descendingIterator();
            return getPostsList(iterator);
        }
        return new ArrayList<>();
    }

    private List<Long> getPostsList(Iterator<TimedPostId> iterator) {
        List<Long> nextTwentyPostIds = new ArrayList<>(postsBatchSize);
        int count = 0;
        while (iterator.hasNext() && count < postsBatchSize) {
            nextTwentyPostIds.add(iterator.next().getPostId());
            count++;
        }
        return nextTwentyPostIds;
    }

    private List<FeedDto> getPostsFromDB(long userId, Long postId) {
        RedisUser user = getOrSaveRedisUser(userId);
        List<FeedDto> feedDtos;
        List<PostDto> feed = null;
        if (postId == null) {
            feed = postService.getFirstPostsForFeed(user.getFolloweeIds(), postsBatchSize);
        } else {
            LocalDateTime publishedAt = postService.getPost(postId).getPublishedAt();
            feed = postService.getNextPostsForFeed(user.getFolloweeIds(), postsBatchSize, publishedAt);
        }

        feedDtos = feed.stream()
                .map(redisPostMapper::toRedisPost)
                .map(redisPost -> {
                    RedisUser redisUser = getOrSaveRedisUser(redisPost.getAuthorId());
                    return buildFeedDto(redisUser, redisPost);
                }).toList();
        return feedDtos;
    }

    private RedisUser getOrSaveRedisUser(long userId) {
        return redisUserRepository.findById(userId)
                .orElseGet(() -> {
                    RedisUser user = redisUserMapper.toEntity(userServiceClient.getUser(userId));
                    redisUserRepository.save(user);
                    return user;
                });
    }

    private RedisPost getOrSaveRedisPost(long postId) {
        return redisPostRepository.findById(postId)
                .orElseGet(() -> {
                    RedisPost post = redisPostMapper.toRedisPost(postService.getPost(postId));
                    redisPostRepository.save(post);
                    return post;
                });
    }

    private FeedDto buildFeedDto(RedisUser user, RedisPost post) {
        return FeedDto.builder()
                .authorId(user.getId())
                .authorName(user.getUsername())
                .smallFileId(user.getSmallFileId())
                .postId(post.getId())
                .content(post.getContent())
                .likes(post.getLikes())
                .redisCommentDtos(post.getRedisCommentDtos())
                .publishedAt(post.getPublishedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
