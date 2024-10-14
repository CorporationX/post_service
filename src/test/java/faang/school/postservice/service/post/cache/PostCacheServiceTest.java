package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {
    private static final int NUMBER_OF_TOP_IN_CACHE = 100;
    private static final String POST_ID_PREFIX = "post:";
    private static final int NUMBER_OF_TAGS = 3;
    private static final long USER_ID = 1L;
    private static final String POST_ID_STR = "post:1";
    private static final LocalDateTime PUBLISHED_AT = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final long TIMESTAMP = PUBLISHED_AT.toInstant(ZoneOffset.UTC).toEpochMilli();
    private static final String TAG = "java";
    private static final boolean TO_DELETE = true;
    private static final boolean NOT_DELETE = false;
    private static final int START = 0;
    private static final int END = 9;
    private static final String PONG = "PONG";

    @Mock
    private RedisTemplate<String, PostCacheDto> redisTemplatePost;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private PostCacheService postCacheService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheService, "numberOfTopInCache", NUMBER_OF_TOP_IN_CACHE);
        ReflectionTestUtils.setField(postCacheService, "postIdPrefix", POST_ID_PREFIX);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Save posts to cache by tag successful")
    void testSavePostsByTag() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        ValueOperations<String, PostCacheDto> valueOperations = mock(ValueOperations.class);

        when(redisTemplatePost.opsForValue()).thenReturn(valueOperations);
        postCacheService.savePostsByTag(TAG, List.of(post));

        verify(valueOperations).set(POST_ID_STR, post);
        verify(zSetOperations).add(TAG, POST_ID_STR, TIMESTAMP);
        verify(zSetOperations).removeRange(TAG, 0, (NUMBER_OF_TOP_IN_CACHE + 1) * -1);
    }

    @Test
    @DisplayName("Given toDeletePost true when check then delete post from cache")
    void testSaveChangesOfPostDeletePost() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);

        postCacheService.saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, TO_DELETE);

        verify(redisTemplatePost).delete(POST_ID_STR);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given toDeletePost false when check then set post to cache")
    void testTryToSaveChangesOfPostAddPost() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
        ValueOperations<String, PostCacheDto> valueOperations = mock(ValueOperations.class);

        when(redisTemplatePost.opsForValue()).thenReturn(valueOperations);
        postCacheService.saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, NOT_DELETE);

        verify(valueOperations).set(POST_ID_STR, post);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given tags for delete and add then delete and add to cache")
    void testTryToSaveChangesOfPostDeleteAddTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
        ValueOperations<String, PostCacheDto> valueOperations = mock(ValueOperations.class);

        when(redisTemplatePost.opsForValue()).thenReturn(valueOperations);
        postCacheService.saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, NOT_DELETE);

        hashTags.forEach(tag -> {
            verify(zSetOperations).remove(tag, POST_ID_STR);
            verify(zSetOperations).add(tag, POST_ID_STR, TIMESTAMP);
            verify(zSetOperations).removeRange(tag, 0, (NUMBER_OF_TOP_IN_CACHE + 1) * -1);
        });
    }

    @Test
    @DisplayName("Given null postIds list when check then return empty list")
    void testFindInRangeByHashTagPostIdsIsNull() {
        when(zSetOperations.reverseRange(TAG, START, END)).thenReturn(null);

        assertThat(postCacheService.findInRangeByHashTag(TAG, START, END)).isEmpty();
    }

    @Test
    @DisplayName("Given RedisConnectionFailureException when catch then return empty list")
    void testFindInRangeByHashTagRedisConnectionException() {
        when(zSetOperations.reverseRange(TAG, START, END)).thenThrow(new RedisConnectionFailureException(""));

        assertThat(postCacheService.findInRangeByHashTag(TAG, START, END)).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Find post of cache in range by tag successful ")
    void testFindInRangeByHashTagSuccessful() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<PostCacheDto> posts = List.of(post);
        Set<String> postIds = Set.of(POST_ID_STR);
        ValueOperations<String, PostCacheDto> valueOperations = mock(ValueOperations.class);

        when(zSetOperations.reverseRange(TAG, START, END)).thenReturn(postIds);
        when(redisTemplatePost.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(postIds)).thenReturn(posts);

        assertThat(postCacheService.findInRangeByHashTag(TAG, START, END)).isEqualTo(posts);
    }

    @Test
    @DisplayName("Test filter by tags in cache successful ")
    void testTryFilterByTagsInCacheSuccessful() {
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);

        when(redisTemplatePost.hasKey(hashTags.get(0))).thenReturn(Boolean.TRUE);
        when(redisTemplatePost.hasKey(hashTags.get(1))).thenReturn(Boolean.TRUE);
        when(redisTemplatePost.hasKey(hashTags.get(2))).thenReturn(Boolean.TRUE);

        assertThat(postCacheService.filterByTagsInCache(hashTags))
                .isEqualTo(hashTags);
    }

    @Test
    @DisplayName("Post is in cache check successful")
    void testPostIsInCacheSuccessful() {
        when(redisTemplatePost.hasKey(POST_ID_STR)).thenReturn(Boolean.TRUE);

        assertThat(postCacheService.postIsInCache(POST_ID_STR)).isTrue();
    }

    @Test
    @DisplayName("Given Redis connection exception when catch then return false")
    void testIsRedisConnectedRedisConnectionException() {
        when(redisTemplatePost.getConnectionFactory()).thenThrow(new RedisConnectionFailureException(""));

        assertThat(postCacheService.isRedisConnected()).isFalse();
    }

    @Test
    @DisplayName("Is Redis connected check successful")
    void testIsRedisConnectedSuccessful() {
        JedisConnectionFactory connectionFactory = mock(JedisConnectionFactory.class);
        RedisConnection redisConnection = mock(RedisConnection.class);

        when(redisTemplatePost.getConnectionFactory()).thenReturn(connectionFactory);
        when(connectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.ping()).thenReturn(PONG);

        assertThat(postCacheService.isRedisConnected()).isTrue();
    }
}