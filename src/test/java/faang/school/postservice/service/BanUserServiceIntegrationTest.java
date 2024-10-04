package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.PostgreSQLContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
public class BanUserServiceIntegrationTest extends PostgreSQLContainerConfig {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BanUserService banUserService;

    @MockBean
    private UserServiceClient userServiceClient;

    @SpyBean
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testBanAuthorsWithUnverifiedPosts_OneBatch() {
        postRepository.deleteAll();

        List<Long> ids = Arrays.asList(1L, 2L);
        List<Post> posts = createPosts(ids, false, false, false, LocalDateTime.now().minusDays(1));
        postRepository.saveAll(posts);

        given(userServiceClient.getMaxUserId()).willReturn(3L);

        banUserService.banAuthorsWithUnverifiedPosts();

        verify(redisTemplate, times(1)).executePipelined(any(RedisCallback.class));
    }

    @Test
    public void testBanAuthorsWithUnverifiedPosts_ManyBatches() {
        postRepository.deleteAll();

        List<Long> ids = Arrays.asList(11L, 22L, 33L, 44L);
        List<Post> posts = createPosts(ids, false, false, false, LocalDateTime.now().minusDays(1));
        postRepository.saveAll(posts);

        given(userServiceClient.getMaxUserId()).willReturn(50L);

        banUserService.banAuthorsWithUnverifiedPosts();

        verify(redisTemplate, times(1)).executePipelined(any(RedisCallback.class));
    }

    private List<Post> createPosts(List<Long> ids, boolean isPublished, boolean isDeleted, boolean isVerified, LocalDateTime verifiedDate) {
        return ids.stream().map(id -> {
            Post post = new Post();
            post.setId(id);
            post.setContent("Test post " + id);
            post.setAuthorId(id);
            post.setPublished(isPublished);
            post.setDeleted(isDeleted);
            post.setVerified(isVerified);
            post.setVerifiedDate(verifiedDate);
            post.setScheduledAt(LocalDateTime.now().minusMinutes(1));
            return post;
        }).collect(Collectors.toList());
    }
}
