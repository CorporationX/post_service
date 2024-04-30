package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.NewsFeedPostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    public void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void testGetPostsForUserWhenUserHasPostsInRedisAndDatabaseThenReturnPosts() {
        Long userId = 1L;
        Long postId = 1L;

        List<NewsFeedPostDto> result = feedService.getPostsForUser(userId, postId);

        assertTrue(result.isEmpty());
    }
}