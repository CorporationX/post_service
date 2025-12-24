package faang.school.postservice.service.posts;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.RedisPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PostCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void save(Post post) {
        String key = redisProperties.getPostsKeyPrefix() + ":" + post.getId();

        RedisPost redisPost = RedisPost.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .build();

        redisTemplate.opsForValue().set(
                key,
                redisPost,
                Duration.ofSeconds(redisProperties.getPostsTtlSeconds())
        );
    }
}