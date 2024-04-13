package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.feed.redis.PostRedisDto;
import faang.school.postservice.dto.feed.redis.UserRedisDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final int retryMaxAttempts = 5;
    private final int retryMultiplier = 5;
    private final int retryDelay = 1000;
    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FeedHeater feedHeater;
    private final ZSetOperations<Long, Object> feeds;
    @Value("${feed.batch_size}")
    private int postFeedBatch = 20;
    @Value("${feed.max_size}")
    private int maxFeedSize = 500;

    @Retryable(retryFor = {FeignException.class, ConnectException.class}, maxAttempts = retryMaxAttempts, backoff =
    @Backoff(delay = retryDelay, multiplier = retryMultiplier))
    public List<FeedDto> getFeed(long postIndex) {
        List<Long> postIds = getPostIds(postIndex);
        if (postIds == null) {
            return null;
        }
        return postIds
                .stream()
                .map(this::getFeedByPostId
                )
                .toList();
    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = retryMaxAttempts, backoff =
    @Backoff(delay = retryDelay, multiplier = retryMultiplier))
    private FeedDto getFeedByPostId(Long postId) {
        PostRedisDto postRedisDto = postRedisRepository.findById(postId)
                .orElseGet(() -> postMapper.toRedisEntity(postService.getPostById(postId)));

        long ownerId = postRedisDto.getOwnerId();
        UserRedisDto userRedisDto = userRedisRepository.findById(ownerId)
                .orElseGet(() -> userMapper.toRedisEntity(userServiceClient.getUser(ownerId)));

        return new FeedDto(postRedisDto, userRedisDto);
    }


    private List<Long> getPostIds(long startIndex) {
        Set<Object> range = feeds.range(userContext.getUserId(), startIndex, startIndex + postFeedBatch - 1);
        if (range == null || range.size() < postFeedBatch) {
            feedHeater.heatFeed();
        }

        if (range == null) {
            return null;
        }
        return range
                .stream()
                .map(o -> (Long) o)
                .toList();
    }

    public void addPostIdToUsersFeed(long postId, List<Long> userIds, LocalDateTime publishedAt) {
        userIds.forEach(userId -> {
            feeds.add(userId, postId, publishedAt.getNano());

            if (feeds.size(userId) > maxFeedSize) {
                feeds.removeRange(userId, maxFeedSize, feeds.size(userId) - 1);
            }
        });
    }
}
