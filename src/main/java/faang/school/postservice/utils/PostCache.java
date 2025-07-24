package faang.school.postservice.utils;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class PostCache {

    private final RedisTemplate<Long, Post> postRedisTemplate;
    private final PostService service;

    @Value("${cache.expiration-hours}")
    private int expirationHours;

    public PostCache(
            @Qualifier("postRedisTemplate") RedisTemplate<Long, Post> objectRedisTemplate,
            PostService service
    ) {
        this.postRedisTemplate = objectRedisTemplate;
        this.service = service;
    }

    public void save(Post post) {
        postRedisTemplate.opsForValue().setIfAbsent(post.getId(), post, Duration.ofHours(expirationHours));
    }

    public void save(PostEventDto post) {
        postRedisTemplate.opsForValue().setIfAbsent(post.postId(), service.getPostById(post.postId()), Duration.ofHours(expirationHours));
    }

    public Post get(Long id) {
        return postRedisTemplate.opsForValue().get(id);
    }

    public boolean delete(Long id) {
        return postRedisTemplate.delete(id);
    }

    public Long deleteAll(List<Long> ids) {
        return postRedisTemplate.delete(ids);
    }
}