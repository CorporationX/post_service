package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {
    private static final int NUMBER_OF_TOP_IN_CACHE = 100;
    private static final int NUMBER_OF_TAGS = 3;
    private static final long USER_ID = 1L;
    private static final String POST_ID_STR = "post:1";
    private static final LocalDateTime PUBLISHED_AT = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final long TIMESTAMP = PUBLISHED_AT.toInstant(ZoneOffset.UTC).toEpochMilli();

    @Mock
    private RedisTemplate<String, PostCacheDto> redisTemplatePost;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private PostCacheService postCacheOperationsTries;

    private final List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
    private final PostCacheDto post = buildPostCacheDto(USER_ID);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheOperationsTries, "numberOfTopInCache", NUMBER_OF_TOP_IN_CACHE);
    }
//
//    @Test
//    @DisplayName("Given toDeletePost true when check then delete post from cache")
//    void testTryToSaveChangesOfPostDeletePost() {
//        when(redisTemplatePost.exec()).thenReturn(List.of(new Object()));
//        postCacheOperationsTries.tryToSaveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, true);
//        verify(redisTemplatePost).delete(POST_ID_STR);
//    }
//
//    @Test
//    @DisplayName("Given toDeletePost false when check then add post to cache")
//    void testTryToSaveChangesOfPostAddPost() {
//        when(redisTemplatePost.exec()).thenReturn(List.of(new Object()));
//        when(redisTemplatePost.opsForValue()).thenReturn(mock(ValueOperations.class));
//
//        postCacheOperationsTries.tryToSaveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, false);
//
//        verify(redisTemplatePost.opsForValue()).set(POST_ID_STR, post);
//    }
//
//    @Test
//    @DisplayName("Given tags for delete and add and delete and add to cache")
//    void testTryToSaveChangesOfPostDeleteAddTags() {
//        when(redisTemplatePost.exec()).thenReturn(List.of(new Object()));
//        postCacheOperationsTries.tryToSaveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, true);
//
//        hashTags.forEach(tag -> {
//            verify(zSetOperations).remove(tag, POST_ID_STR);
//            verify(zSetOperations).add(tag, POST_ID_STR, TIMESTAMP);
//            verify(zSetOperations).removeRange(tag, 0, (NUMBER_OF_TOP_IN_CACHE + 1) * -1);
//        });
//    }
//
//    @Test
//    @DisplayName("Transaction discarded test")
//    void testTryToSaveChangesOfPostTransactionDiscarded() {
//        when(redisTemplatePost.exec()).thenReturn(List.of());
//
//        assertThatThrownBy(() -> postCacheOperationsTries
//                .tryToSaveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, hashTags, true))
//                .isInstanceOf(RedisTransactionInterrupted.class)
//                .hasMessageContaining(REDIS_TRANSACTION_INTERRUPTED, post.getId());
//
//        verify(redisTemplatePost).discard();
//    }
//
//    @Test
//    @DisplayName("Test filter by tags in cache successful ")
//    void testTryFilterByTagsInCacheSuccessful() {
//        when(redisTemplatePost.hasKey(hashTags.get(0))).thenReturn(Boolean.TRUE);
//        when(redisTemplatePost.hasKey(hashTags.get(1))).thenReturn(Boolean.TRUE);
//
//        assertThat(postCacheOperationsTries.tryFilterByTagsInCache(hashTags, hashTags.get(2)))
//                .isEqualTo(hashTags);
//    }
}