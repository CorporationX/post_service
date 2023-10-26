package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedService { // for ids of posts
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostMapper redisPostMapper;

    @Value("{post.feed.post-size}")
    private int feedSizeOfPosts;

    public List<FeedDto> getFeed(long postId) {
        long userId = userContext.getUserId();
        Optional<RedisFeed> redisFeed = redisFeedRepository.findById(userId);
        if (redisFeed.isEmpty()) {
            return getPostsFromDb(userId, postId);
        }
        List<Long> twentyIdsOfPosts = getTwentyIdsOfPosts(postId, redisFeed.get());
        if (twentyIdsOfPosts.isEmpty()) {
            return getPostsFromDb(userId, postId);
        }
        List<FeedDto> resultFeed = new ArrayList<>();
        twentyIdsOfPosts.stream()
                .map(id -> checkPostInRedisPost(postId))
                .forEach(redisPost -> {
            RedisUser redisUser = checkUserInRedisUser(redisPost.getAuthorId());
            resultFeed.add(buildFeedDto(redisPost, redisUser));
        });
        return resultFeed;
    }

    private List<FeedDto> getPostsFromDb(long userId, Long postId) {
        RedisUser user = checkUserInRedisUser(userId); // get юзера
        List<FeedDto> feedsDto; // фиды для юзеров
        List<PostDto> postsDto;
        if (postId == null) { // в фиде нет ниодного поста
            postsDto = postService.getPostsFromBeginning(user.getFolloweeIds(), feedSizeOfPosts);
        } else { // пост есть -> начинаем брать посты начиная с этого поста который есть в кэше
            postsDto = postService.getPostsFromPoint(user.getFolloweeIds(), feedSizeOfPosts, postId);
        }
        feedsDto = postsDto.stream()
                .map(redisPostMapper::toRedisPost) // мап постов в "сущность" пост редис
                .map(redisPost -> {
                    RedisUser redisUser = checkUserInRedisUser(redisPost.getAuthorId());
                    return buildFeedDto(redisPost, redisUser);
                }).toList();
        return feedsDto;
    }

    private RedisUser checkUserInRedisUser(long userId) { // юзер либо в userFeed, либо в user_service
        return redisUserRepository.findById(userId)
                .orElseGet(() -> {
                    RedisUser userFromContext = redisUserMapper.toRedisUser(userServiceClient.getUser(userId));
                    redisUserRepository.save(userFromContext);
                    return userFromContext;
                });
    }

    private RedisPost checkPostInRedisPost(long postId) { // пост либо в feed, либо в post_service
        return redisPostRepository.findById(postId)
                .orElseGet(() -> {
                    RedisPost postFromPostService = redisPostMapper.toRedisPost(postService.getPost(postId));
                    redisPostRepository.save(postFromPostService);
                    return postFromPostService;
                });
    }

    private List<Long> getTwentyIdsOfPosts(Long postId, RedisFeed feed) {
        LinkedHashSet<Long> postIds = feed.getPostIds();
        // TODO
        // если пост есть в кэше - берем 20 постов (есть ли там 20 вообще)
        // если нету - идем в бд за 20-ью постами
    }

    private FeedDto buildFeedDto(RedisPost redisPost, RedisUser redisUser) { // вид фида для юзера
        return FeedDto.builder()
                .postId(redisPost.getId())
                .authorName(redisUser.getUsername())
                .content(redisPost.getContent())
                .likes(redisPost.getPostLikes())
                .comments(redisPost.getComments())
                .createdAt(redisPost.getPublishedAt())
                .updatedAt(redisPost.getUpdatedAt())
                .build();
    }
}
