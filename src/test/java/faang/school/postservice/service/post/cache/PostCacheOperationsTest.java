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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
    private static final LocalDateTime PUBLISHED_AT = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final long TIMESTAMP = PUBLISHED_AT.toInstant(ZoneOffset.UTC).toEpochMilli();
    private static final long ZERO_TIMESTAMP = 0;
    private static final boolean TO_DELETE = true;
    private static final boolean NOT_DELETE = false;
    private static final List<String> EMPTY_NEW_TAGS = List.of();
    private static final List<String> EMPTY_DEL_TAGS = List.of();

    @Spy
    private PostHashTagParser postHashTagParser;

    @Mock
    private PostCacheService postCacheService;

    @InjectMocks
    private PostCacheOperations postCacheOperations;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCacheOperations, "postIdPrefix", POST_ID_PREFIX);
    }

    @Test
    @DisplayName("Given post with no new tags and will not save changes to cache by tag")
    void testAddPostToCacheByNoNewTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(emptyHashTags)).thenReturn(emptyHashTags);
        postCacheOperations.addPostToCache(post, emptyHashTags);

        verify(postCacheService, times(NEVER)).saveChangesOfPost(any(PostCacheDto.class), anyString(), anyLong(),
                anyList(), anyList(), anyBoolean());
    }

    @Test
    @DisplayName("Given post with new tags and save changes to cache")
    void testAddPostToCacheSuccessful() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);

        when(postCacheService.filterByTagsInCache(hashTags)).thenReturn(hashTags);
        postCacheOperations.addPostToCache(post, hashTags);

        verify(postCacheService).saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, EMPTY_DEL_TAGS, NOT_DELETE);
    }

    @Test
    @DisplayName("Given post with no primal tags and no in cache and will not save changes to cache")
    void testDeletePostOfCacheNoPrimalTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(emptyHashTags)).thenReturn(emptyHashTags);
        postCacheOperations.deletePostOfCache(post, emptyHashTags);

        verify(postCacheService, times(NEVER)).saveChangesOfPost(any(PostCacheDto.class), anyString(), anyLong(),
                anyList(), anyList(), anyBoolean());
    }

    @Test
    @DisplayName("Given post with primal tags and save changes to cache")
    void testDeletePostOfCacheSuccessfulPrimalTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);

        when(postCacheService.filterByTagsInCache(hashTags)).thenReturn(hashTags);
        postCacheOperations.deletePostOfCache(post, hashTags);

        verify(postCacheService)
                .saveChangesOfPost(post, POST_ID_STR, ZERO_TIMESTAMP, EMPTY_NEW_TAGS, hashTags, TO_DELETE);
    }

    @Test
    @DisplayName("Given post with no primal tags and in cache and save changes to cache")
    void testDeletePostOfCacheSuccessfulInCache() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(emptyHashTags)).thenReturn(emptyHashTags);
        when(postCacheService.postIsInCache(POST_ID_STR)).thenReturn(true);
        postCacheOperations.deletePostOfCache(post, emptyHashTags);

        verify(postCacheService)
                .saveChangesOfPost(post, POST_ID_STR, ZERO_TIMESTAMP, EMPTY_NEW_TAGS, EMPTY_DEL_TAGS, TO_DELETE);
    }

    @Test
    @DisplayName("Given post with tags for deleted and save changes to cache")
    void testUpdatePostOfCacheHaveDelTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(emptyHashTags)).thenReturn(hashTags);
        postCacheOperations.updatePostOfCache(post, hashTags, emptyHashTags);

        verify(postCacheService)
                .saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, EMPTY_DEL_TAGS, NOT_DELETE);
    }

    @Test
    @DisplayName("Given post with new tags and save changes to cache ")
    void testUpdatePostOfCacheHaveNewTags() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(hashTags)).thenReturn(hashTags);
        postCacheOperations.updatePostOfCache(post, emptyHashTags, hashTags);

        verify(postCacheService)
                .saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, hashTags, EMPTY_DEL_TAGS, NOT_DELETE);
    }

    @Test
    @DisplayName("Given post no have tags for save but is in cache and save changes to cache")
    void testUpdatePostOfCacheInCacheTrue() {
        PostCacheDto post = buildPostCacheDto(USER_ID, PUBLISHED_AT);
        List<String> emptyHashTags = List.of();

        when(postCacheService.filterByTagsInCache(emptyHashTags)).thenReturn(emptyHashTags);
        when(postCacheService.postIsInCache(POST_ID_STR)).thenReturn(true);
        postCacheOperations.updatePostOfCache(post, emptyHashTags, emptyHashTags);

        verify(postCacheService)
                .saveChangesOfPost(post, POST_ID_STR, TIMESTAMP, EMPTY_NEW_TAGS, EMPTY_DEL_TAGS, TO_DELETE);
    }
}