package faang.school.postservice.service.posts.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.redis.model.RedisPost;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class PostRedisMapperTest {

    private final PostRedisMapper mapper = new PostRedisMapper();

    @Test
    void toRedis_shouldMapAllFields() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(2L);
        post.setProjectId(3L);
        post.setContent("hello");

        RedisPost redisPost = mapper.toRedis(post);

        assertThat(redisPost.getId()).isEqualTo(1L);
        assertThat(redisPost.getAuthorId()).isEqualTo(2L);
        assertThat(redisPost.getProjectId()).isEqualTo(3L);
        assertThat(redisPost.getContent()).isEqualTo("hello");
    }
}