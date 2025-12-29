package faang.school.postservice.cache;

import faang.school.postservice.cache.repository.AuthorCacheRepository;
import faang.school.postservice.cache.repository.FeedCacheRepository;
import faang.school.postservice.cache.repository.PostCacheRepository;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.author.AuthorMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeaterTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private AuthorCacheRepository authorCacheRepository;
    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private FeedCacheRepository feedCacheRepository;
    @Mock
    private UserService userService;
    @Mock
    private AuthorMapper authorMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private FeedHeater feedHeater;

    @BeforeEach
    void setUp() {
        feedHeater = new FeedHeater(postRepository, authorCacheRepository,
                postCacheRepository, feedCacheRepository, userService,
                authorMapper, postMapper, redisTemplate);

        ReflectionTestUtils.setField(feedHeater, "postsCacheSize", 500);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testHeat_WhenLockNotAcquired_ShouldThrowException() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), Mockito.any()))
                .thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> feedHeater.heat(List.of(1L, 2L)));

        assertEquals("Прогрев уже выполняется", exception.getMessage());
        verify(postRepository, never()).findFeedPosts(anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void testHeat_ShouldReleaseLockOnException() {
        List<Long> followerIds = List.of(1L);

        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), Mockito.any()))
                .thenReturn(true);

        when(postRepository.findFeedPostsForHeating(anyLong(), Mockito.any(Pageable.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> feedHeater.heat(followerIds));

        verify(redisTemplate).delete("heat:lock");
    }

    @Test
    void testHeat_ShouldUseParallelStreamForFollowerPosts() {
        List<Long> followerIds = List.of(1L, 2L, 3L);
        List<Post> mockPosts = List.of(Post.builder().id(100L).authorId(10L).build());

        when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), Mockito.any()))
                .thenReturn(true);
        when(postRepository.findFeedPostsForHeating(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(mockPosts);

        feedHeater.heat(followerIds);

        verify(feedCacheRepository, times(3)).saveAll(anyLong(), anyList());
    }
}