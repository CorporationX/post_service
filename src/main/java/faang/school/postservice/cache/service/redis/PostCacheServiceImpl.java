package faang.school.postservice.cache.service.redis;

import faang.school.postservice.cache.service.PostCacheService;
import faang.school.postservice.dto.post.PostViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * PostCacheServiceImpl — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {
    private static final String HASHTAG_COUNT_KEY = "hashtag_count";
    private static final long MAX_POSTS_COUNT = 50;
    private static final int FIRST_INDEX = 0;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, List<PostViewDto>> redisTemplate;

    @Override
    public void addPost(String hashtag, PostViewDto post) {
        List<PostViewDto> posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            posts = new LinkedList<>();
        }
        posts.add(post);
        if (posts.size() > MAX_POSTS_COUNT) {
            posts.remove(FIRST_INDEX);
        }
        redisTemplate.opsForValue().set(hashtag, posts);
        stringRedisTemplate.opsForZSet().incrementScore(HASHTAG_COUNT_KEY, hashtag, 1);
        log.info("успешно создали связь между постом с id {} и хэштега '{}'", post.id(), hashtag);
    }

    @Override
    public void deletePost(String hashtag, PostViewDto post) {
        List<PostViewDto> posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            return;
        }

        var success = posts.remove(post);
        if (!success) {
            log.warn("удаление связи поста с id {} и хэштега '{}' не удалась", post, hashtag);
            return;
        }
        stringRedisTemplate.opsForZSet().incrementScore(HASHTAG_COUNT_KEY, hashtag, -1);
    }

    @Override
    public List<String> getPopularHashtags(long offset, long limit) {
        Set<ZSetOperations.TypedTuple<String>> hashtags = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(HASHTAG_COUNT_KEY, offset, offset + limit);
        if (hashtags == null) {
            return Collections.emptyList();
        }

        return hashtags.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
    }

    @Override
    public List<PostViewDto> getList(String hashtag) {
        var posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            return Collections.emptyList();
        }

        return posts;
    }

}
