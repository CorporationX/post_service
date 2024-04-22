package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.redis.PostIdDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    @Value("${feed.batch_size.post}")
    private int postFeedBatch = 20;
    @Value("${feed.max_size}")
    private int maxFeedSize = 500;

    public List<FeedDto> getFeed(Long lastPostId) {
        long userId = userContext.getUserId();
        List<PostIdDto> postIdDtos = new ArrayList<>();

        redisFeedRepository.findById(userId)
                .ifPresent(
                        (redisFeed) -> {
                            TreeSet<PostIdDto> postIdsDtosFromCache = redisFeed.getPostIds();
                            if (lastPostId == null) {
                                postIdDtos.addAll(postIdsDtosFromCache);
                            } else {
                                postIdsDtosFromCache.stream()
                                        .filter(postIdDto -> lastPostId == postIdDto.getPostId())
                                        .findFirst()
                                        .ifPresent(postIdDto -> {
                                            SortedSet<PostIdDto> sortedSet = postIdsDtosFromCache.tailSet(postIdDto);
                                            sortedSet.remove(sortedSet.first());
                                            postIdDtos.addAll(sortedSet);
                                        });
                            }
                        }
                );

        if (postIdDtos.size() < postFeedBatch) {
            List<Long> followerIds = userServiceClient.getFollowerIdsById(userId);

            postIdDtos.addAll(
                    postService //posts from DB
                            .getPosts(followerIds, lastPostId, postFeedBatch - postIdDtos.size())
                            .stream()
                            .map(post -> new PostIdDto(post.getId(), post.getPublishedAt()))
                            .toList()
            );
        }

        return postIdDtos.stream()
                .limit(postFeedBatch)
                .map(postIdDto -> getFeedDto(userId, postIdDto.getPostId()))
                .toList();
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5)
    private FeedDto getFeedDto(long userId, long postId) {
        RedisUser redisUser = redisUserRepository.findById(userId)
                .orElseGet(() -> redisUserMapper.toEntity(userServiceClient.getUser(userId)));

        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseGet(() -> redisPostMapper.toEntity(postService.getPostDto(postId)));

        return FeedDto.builder()
                .userId(redisUser.getId())
                .username(redisUser.getUsername())
                .postId(redisPost.getId())
                .content(redisPost.getContent())
                .likes(redisPost.getLikes())
                .comments(redisPost.getComments().stream().toList())
                .publishedAt(redisPost.getPublishedAt())
                .updatedAt(redisPost.getUpdatedAt())
                .build();
    }
}
