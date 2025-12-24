package faang.school.postservice.cache;

import faang.school.postservice.cache.model.post.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepository {

    @Value("${cache.posts.collection}")
    private String collection;

    private final StringRedisTemplate redisTemplate;

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
                    .authorId(Long.parseLong(PostCache.Fields.authorId))
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
}