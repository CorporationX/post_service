package faang.school.postservice.cache;

import faang.school.postservice.cache.dto.CachedPost;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedCache {

    @Value("${spring.data.redis.cache_key.feed.prefix}")
    private String feedPrefix;

    @Value("${spring.data.redis.cache_key.feed.batch_size}")
    private int batchSize;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserContext userContext;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;

    public void save(Long userId, Long postId) {
        String key = feedPrefix + userId;
        redisTemplate.opsForZSet().add(key, String.valueOf(postId), System.currentTimeMillis());
    }

    public List<CachedPost> getFeed(Long startPostId) {
        long userId = userContext.getUserId();
        ZSetOperations<String, Object> objectZSetOperations = redisTemplate.opsForZSet();

        startPostId = startPostId == null ? 1L : startPostId;

        Set<Object> postIdsCached = objectZSetOperations.reverseRange(feedPrefix + userId, startPostId, startPostId + batchSize - 1);

        if (postIdsCached == null || postIdsCached.isEmpty()) {
            return findPostInRepository(userId, startPostId, batchSize);
        }

        List<Long> postIds = postIdsCached.stream().map(object -> (Long) object).toList();
        List<CachedPost> cachedPosts = toCachedPost(postIds);

        if (cachedPosts.size() < batchSize) {
            List<CachedPost> postInRepository = findPostInRepository(userId, startPostId, batchSize - cachedPosts.size());
            cachedPosts.addAll(postInRepository);
        }
        return cachedPosts;
    }

    private List<CachedPost> toCachedPost(List<Long> posts) {
        return posts.stream()
                .map(postId -> postCacheRepository.findById(postId)
                        .orElseGet(() -> postMapper.toCachedPost(postService.getPost(postId)))).collect(Collectors.toList());
    }

    private List<CachedPost> findPostInRepository(Long userId, Long startPostId, int batchSize) {
        List<UserDto> userFollowers = userServiceClient.getUserFollowers(userId);
        List<Long> authorIds = userFollowers.stream().map(UserDto::getId).collect(Collectors.toList());
        List<CachedPost> postDtos = postService.getPostsByAuthorIds(authorIds, startPostId, batchSize);
        postDtos.forEach(post -> save(userId, post.getId()));
        return postDtos;
    }
}
