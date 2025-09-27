package faang.school.postservice.repository.redis;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Класс для сохранения поста в коллекцию {@code posts} в Redis
 *
 * @author Linempy
 * @since 26.09.2025
 */
@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    @Value("${redis.schema.post.key}")
    private String keyPost;

    @Value("${redis.schema.post.ttl-day")
    private int ttlDay;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;

    public void savePost(Post post) {
        String key = getKeyForPost(post.getId());
        redisTemplate.opsForValue().set(key, post, Duration.ofDays(ttlDay));
    }

    public Post getPost(Long id) {
        String key = getKeyForPost(id);

        return Optional.ofNullable((Post) redisTemplate.opsForValue().get(key))
            .orElseGet(() -> {
                    Post postFromDb = postRepository.findPostOrThrow(id);
                    savePost(postFromDb);
                    return postFromDb;
                }
            );
    }

    private String getKeyForPost(Long id) {
        return String.format(keyPost, id);
    }


}