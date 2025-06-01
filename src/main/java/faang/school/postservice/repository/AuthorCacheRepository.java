package faang.school.postservice.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorCacheRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final String keyPrefix;

    public AuthorCacheRepository(StringRedisTemplate stringRedisTemplate,
                                 @Value("${app.cache.authors.key-prefix}") String keyPrefix) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.keyPrefix = keyPrefix;
    }

    public void saveAuthor(Long postId, Long authorId) {
        stringRedisTemplate.opsForValue().set(keyPrefix + postId, authorId.toString());
    }

    public Optional<Long> findAuthorByPostId(Long postId) {
        String authorId = stringRedisTemplate.opsForValue().get(keyPrefix + postId);
        return Optional.ofNullable(authorId).map(Long::valueOf);
    }

    public void deleteAuthor(Long postId) {
        stringRedisTemplate.delete(keyPrefix + postId);
    }
}
