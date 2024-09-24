package faang.school.postservice.service.post.cache;


import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheOperationsTest {
    private static final String POST_ID_PREFIX = "post:";
    private static final int NUMBER_OF_TAGS = 3;
    private static final int NUMBER_OF_POSTS = NUMBER_OF_TAGS;
    private static final long USER_ID = 1L;
    private static final int NEVER = 0;
    private static final String TAG = "java";
    private static final int START = 0;
    private static final int END = 9;

    @Spy
    private PostHashTagService postHashTagService;

    @Mock
    private PostCacheOperationsTries postCacheOperationsTries;

    @Mock
    private RedisTemplate<String, PostCacheDto> redisTemplatePost;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private PostCacheOperations postCacheOperations;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheOperations, "postIdPrefix", POST_ID_PREFIX);
    }

    @Test
    @DisplayName("Trying to find ids when redis no connect then throw exception")
    void testFindIdsByHashTagRedisNoConnect() {
        when(zSetOperations.reverseRange(TAG, START, END)).thenThrow(RedisConnectionFailureException.class);
        assertThat(postCacheOperations.findIdsByHashTag(TAG, START, END))
                .isEmpty();
    }

    @Test
    @DisplayName("Find post ids by tag in range and return set of ids")
    void testFindIdsByHashTagSuccessful() {
        postCacheOperations.findIdsByHashTag(TAG, START, END);

        verify(zSetOperations).reverseRange(TAG, START, END);
    }

    @Test
    @DisplayName("Trying to find posts when redis no connect then throw exception")
    void testFindAllByIdsRedisNoConnect() {
        List<String> postIds = List.of("post:1", "post:2", "post:3");
        when(redisTemplatePost.opsForValue()).thenThrow(RedisConnectionFailureException.class);
        assertThat(postCacheOperations.findAllByIds(postIds))
                .isEmpty();
    }

    @Test
    @DisplayName("Find all posts from cache by ids and return")
    void testFindAllByIdsSuccessful() {
        List<String> postIds = List.of("post:1", "post:2", "post:3");
        when(redisTemplatePost.opsForValue()).thenReturn(mock(ValueOperations.class));
        postCacheOperations.findAllByIds(postIds);

        verify(redisTemplatePost.opsForValue()).multiGet(postIds);
    }

    @Test
    @DisplayName("Given ")
    void test() {

    }

}