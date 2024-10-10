package faang.school.postservice.service.post.cache;


import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
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
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheOperationsTest {
    private static final String POST_ID_PREFIX = "post:";
    private static final String POST_ID_STR = "post:1";
    private static final int NUMBER_OF_TAGS = 3;
    private static final long USER_ID = 1L;
    private static final int NEVER = 0;
    private static final String TAG = "java";
    private static final int START = 0;
    private static final int END = 9;
    private static final LocalDateTime PUBLISHED_AT = LocalDateTime.of(2000, 1, 1, 1, 1);

    @Spy
    private PostHashTagParser postHashTagParser;

    @Mock
    private PostCacheService postCacheService;

    @Mock
    private RedisTemplate<String, PostCacheDto> redisTemplatePost;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private PostCacheOperations postCacheOperations;

    private final List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
    private final List<String> emptyHashTags = List.of();
    private final PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheOperations, "postIdPrefix", POST_ID_PREFIX);
    }

//    @Test
//    @DisplayName("Trying to find ids when redis no connect then throw exception")
//    void testFindIdsByHashTagRedisNoConnect() {
//        when(zSetOperations.reverseRange(TAG, START, END)).thenThrow(RedisConnectionFailureException.class);
//        assertThat(postCacheOperations.findIdsByHashTag(TAG, START, END))
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("Find post ids by tag in range and return set of ids")
//    void testFindIdsByHashTagSuccessful() {
//        postCacheOperations.findIdsByHashTag(TAG, START, END);
//
//        verify(zSetOperations).reverseRange(TAG, START, END);
//    }
//
//    @Test
//    @DisplayName("Trying to find posts when redis no connect then throw exception")
//    void testFindAllByIdsRedisNoConnect() {
//        List<String> postIds = List.of("post:1", "post:2", "post:3");
//        when(redisTemplatePost.opsForValue()).thenThrow(RedisConnectionFailureException.class);
//        assertThat(postCacheOperations.findAllByIds(postIds))
//                .isEmpty();
//    }
//
//    @Test
//    @DisplayName("Find all posts from cache by ids and return")
//    void testFindAllByIdsSuccessful() {
//        List<String> postIds = List.of("post:1", "post:2", "post:3");
//        when(redisTemplatePost.opsForValue()).thenReturn(mock(ValueOperations.class));
//        postCacheOperations.findAllByIds(postIds);
//
//        verify(redisTemplatePost.opsForValue()).multiGet(postIds);
//    }
//
//    @Test
//    @DisplayName("Given post with no new tags and will not save changes to cache by tag")
//    void testAddPostToCacheByTagNoNewTags() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, TAG)).thenReturn(emptyHashTags);
//        postCacheOperations.addPostToCacheByTag(post, emptyHashTags, TAG);
//
//        verify(redisTemplatePost, times(NEVER)).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with new tags and save changes to cache by tag")
//    void testAddPostToCacheByTagSuccessful() {
//        when(postCacheService.tryFilterByTagsInCache(hashTags, TAG)).thenReturn(hashTags);
//        postCacheOperations.addPostToCacheByTag(post, hashTags, TAG);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with no new tags and will not save changes to cache")
//    void testAddPostToCacheNoNewTags() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(emptyHashTags);
//        postCacheOperations.addPostToCache(post, emptyHashTags);
//
//        verify(redisTemplatePost, times(NEVER)).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with new tags and save changes to cache")
//    void testAddPostToCacheSuccessful() {
//        when(postCacheService.tryFilterByTagsInCache(hashTags, null)).thenReturn(hashTags);
//        postCacheOperations.addPostToCache(post, hashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with no primal tags and no in cache and will not save changes to cache")
//    void testDeletePostOfCacheNoPrimalTags() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(emptyHashTags);
//        when(redisTemplatePost.hasKey(POST_ID_STR)).thenReturn(false);
//        postCacheOperations.deletePostOfCache(post, emptyHashTags);
//
//        verify(redisTemplatePost, times(NEVER)).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with primal tags and save changes to cache")
//    void testDeletePostOfCacheSuccessfulPrimalTags() {
//        when(postCacheService.tryFilterByTagsInCache(hashTags, null)).thenReturn(hashTags);
//        postCacheOperations.deletePostOfCache(post, hashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with no primal tags and in cache and save changes to cache")
//    void testDeletePostOfCacheSuccessfulInCache() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(emptyHashTags);
//        when(redisTemplatePost.hasKey(POST_ID_STR)).thenReturn(true);
//        postCacheOperations.deletePostOfCache(post, emptyHashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with tags for deleted and save changes to cache")
//    void testUpdatePostOfCacheHaveDelTags() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(hashTags);
//        postCacheOperations.updatePostOfCache(post, hashTags, emptyHashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post with new tags and save changes to cache ")
//    void testUpdatePostOfCacheHaveNewTags() {
//        when(postCacheService.tryFilterByTagsInCache(hashTags, null)).thenReturn(hashTags);
//        postCacheOperations.updatePostOfCache(post, emptyHashTags, hashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given post no have tags for save but is cache and save changes to cache")
//    void testUpdatePostOfCacheInCacheTrue() {
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(hashTags);
//        postCacheOperations.updatePostOfCache(post, emptyHashTags, emptyHashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
//
//    @Test
//    @DisplayName("Given redis disconnect when throw exception return false")
//    void testIsRedisConnectedFalse() {
//        when(redisTemplatePost.getConnectionFactory()).thenThrow(new RedisConnectionFailureException(""));
//
//        assertThat(postCacheOperations.isRedisConnected())
//                .isFalse();
//    }
//
//    @Test
//    @DisplayName("Given post with changes and save to cache")
//    void testSaveChangesOfPostSuccessful() {
//        when(postCacheService.tryFilterByTagsInCache(hashTags, null)).thenReturn(hashTags);
//        when(postCacheService.tryFilterByTagsInCache(emptyHashTags, null)).thenReturn(hashTags);
//        postCacheOperations.updatePostOfCache(post, emptyHashTags, hashTags);
//
//        verify(redisTemplatePost).execute(any(SessionCallback.class));
//    }
}