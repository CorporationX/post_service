package faang.school.postservice.cache;

import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsFeedCache implements RedisCache {

    private static final String USER_CACHE_KEY_PREFIX = "user_cache:";
    private static final String POST_CACHE_KEY_PREFIX = "post_cache:";
    private static final String FEED_CACHE_KEY_PREFIX = "feed_cache:";

    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisNewsFeedTemplate;

    @Value("${spring.data.redis-news-feed.size}")
    private int feedSizeLimit;

    private DefaultRedisScript<Long> addAndTrimScript;

    @PostConstruct
    public void init() {
        addAndTrimScript = new DefaultRedisScript<>();
        addAndTrimScript.setScriptText("""
                    redis.call('ZADD', KEYS[1], unpack(ARGV, 2))
                    redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -tonumber(ARGV[1] + 1))
                    return redis.call('ZCARD', KEYS[1])
                """);
        addAndTrimScript.setResultType(Long.class);
    }

    @Override
    public void putUser(Long user) {
        String key = USER_CACHE_KEY_PREFIX + user;
        redisNewsFeedTemplate.opsForValue().set(key, user);
        log.info("Cached user with key: {}", key);
    }

    @Override
    public void putPost(RedisPostDto post) {
        String key = POST_CACHE_KEY_PREFIX + post.getId();
        redisNewsFeedTemplate.opsForValue().set(key, post);
//        log.info("Cached post with key: {}", key);
    }

    @Override
    public void putFeed(Long userId, Long postId, LocalDateTime postCreatedAt) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        redisNewsFeedTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        log.info("Added post {} to user {} feed with score {}", postId, userId, postCreatedAt);
    }

    @Override
    public RedisUserDto getUser(Long userId) {
        String key = USER_CACHE_KEY_PREFIX + userId;
        return (RedisUserDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = USER_CACHE_KEY_PREFIX + postId;
        return (RedisPostDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public Set<RedisPostDto> getFeed(Long userId, long start, long end) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        Set<Object> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null) {
            // тут идём в бд и тащим фид от туда
            return Set.of();
        }

        return postIds.stream()
                .map(id -> getPost((Long) id))
                .collect(Collectors.toSet());
    }

    @Async("redisTaskExecutor")
    @Override
    public void putFeedForSubscribers(Long user, List<Long> followerIds) {
        Pageable pageable = PageRequest.of(0, feedSizeLimit);
        List<RedisPostDto> posts = postRepository.findLatestPostsByAuthorId(user, pageable);
        log.info("found {} Posts for user with ID {}", posts.size(), user);

        if (posts.isEmpty()) {
            log.info("No posts found for user {}. Skipping feed update for followers.", user);
            return;
        }
        putUser(user);

        posts.forEach(this::putPost);

        List<Object> scriptArgs = posts.stream()
                .flatMap(post -> {
                    double score = post.getCreatedAt().toEpochSecond(ZoneOffset.UTC) * 1000.0;
                    return Stream.of(score, (Object) post.getId());
                })
                .collect(Collectors.toList());

        followerIds.forEach(followerId -> {
            putFeedBatch(followerId, scriptArgs);
        });
    }

    public void putFeedBatch(Long userId, List<Object> scriptArgs) {
        String key = FEED_CACHE_KEY_PREFIX + userId;

        // Создаем единый список, включающий feedSizeLimit и остальные аргументы.
        // Это предотвращает ошибки, связанные с передачей массива в varargs.
        List<Object> allScriptArgs = new ArrayList<>(scriptArgs.size() + 1);
        allScriptArgs.add(feedSizeLimit);
        allScriptArgs.addAll(scriptArgs);

//        log.info("scriptArgs: {}", allScriptArgs);
        redisNewsFeedTemplate.execute(
                addAndTrimScript,
                Collections.singletonList(key),
                allScriptArgs.toArray()
        );
//        log.info("range 0-500: {}", redisNewsFeedTemplate.opsForZSet().range(key, 0, 500));
    }
}