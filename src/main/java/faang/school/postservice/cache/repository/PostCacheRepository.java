package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.model.post.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    @Value("${cache.posts.collection}")
    private String collection;

    @Value("${cache.posts.ttl-days}")
    private int ttlDays;

    private final StringRedisTemplate redisTemplate;

    public void save(PostCache postCache) {
        String key = collection + postCache.id();

        Map<String, String> hashData = converToMap(postCache);

        redisTemplate.opsForHash().putAll(key, hashData);
        log.debug("Saved new post to cache. Key {}, post id {}", key, postCache.id());

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
    }

    public void saveAll(List<PostCache> postCaches) {
        for (PostCache cache : postCaches) {
            String key = collection + cache.id();

            Map<String, String> data = converToMap(cache);

            redisTemplate.opsForHash().putAll(key, data);
            redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
        }
    }

    public Optional<PostCache> get(Long postId) {
        String key = collection + postId;

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            log.debug("Post {} not found in post cache repository", postId);
            return Optional.empty();
        }

        try {
            PostCache postCache = PostCache.builder()
                    .id(postId)
                    .content(entries.get(PostCache.Fields.content).toString())
                    .authorId(Long.parseLong(entries.get(PostCache.Fields.authorId).toString()))
                    .publishedAt(LocalDateTime.parse(entries.get(PostCache.Fields.publishedAt).toString()))
                    .likesCount(Integer.parseInt(entries.get(PostCache.Fields.likesCount).toString()))
                    .commentsCount(Integer.parseInt(entries.get(PostCache.Fields.commentsCount).toString()))
                    .build();
            return Optional.of(postCache);
        } catch (Exception e) {
            log.error("Failed to parse cached post {}: {}", postId, e.getMessage());
            return Optional.empty();
        }
    }

    private Map<String, String> converToMap(PostCache postCache) {
        return new HashMap<>() {{
            put(PostCache.Fields.id, postCache.id().toString());
            put(PostCache.Fields.content, postCache.content());
            put(PostCache.Fields.authorId, postCache.authorId().toString());
            put(PostCache.Fields.publishedAt, postCache.publishedAt().toString());
            put(PostCache.Fields.likesCount, postCache.likesCount().toString());
            put(PostCache.Fields.commentsCount, postCache.commentsCount().toString());
        }};
    }
}