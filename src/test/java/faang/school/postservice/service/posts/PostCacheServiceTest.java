package faang.school.postservice.service.posts;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.RedisPost;
import faang.school.postservice.service.posts.mapper.PostRedisMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisProperties redisProperties;

    @Mock
    private PostRedisMapper postRedisMapper;

    @InjectMocks
    private PostCacheService postCacheService;

    @Test
    void save_shouldPutPostIntoRedisWithCorrectKeyAndTTL() {
        Post post = new Post();
        post.setId(42L);
        post.setAuthorId(7L);
        post.setProjectId(3L);
        post.setContent("test content");

        RedisPost redisPost = RedisPost.builder()
                .id(42L)
                .authorId(7L)
                .projectId(3L)
                .content("test content")
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisProperties.getPostsKeyPrefix()).thenReturn("posts");
        when(redisProperties.getPostsTtlSeconds()).thenReturn(86400L);
        when(postRedisMapper.toRedis(post)).thenReturn(redisPost);

        postCacheService.save(post);

        verify(valueOperations).set(
                eq("posts:42"),
                any(RedisPost.class),
                eq(Duration.ofSeconds(86400L))
        );
    }
}