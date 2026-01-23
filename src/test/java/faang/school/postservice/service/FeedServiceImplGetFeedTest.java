package faang.school.postservice.service;

import faang.school.postservice.cache.PostCacheRepositoryImpl;
import faang.school.postservice.cache.UserCacheRepositoryImpl;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedRedisProperties;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.feed.FeedPostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.feed.FeedPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedDbRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplGetFeedTest {

    private static final long USER_ID = 10L;

    private static final long AUTHOR_ID_1 = 501L;
    private static final long AUTHOR_ID_2 = 502L;

    private static final String KEY_PREFIX = "feed:";
    private static final LocalDateTime T1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 1, 1, 11, 0);

    @Mock private FeedDbRepository feedDbRepository;
    @Mock private UserServiceClient userServiceClient;
    @Mock private PostRepository postRepository;
    @Mock private PostCacheRepositoryImpl postCacheRepository;
    @Mock private UserCacheRepositoryImpl userCacheRepository;

    @Mock private StringRedisTemplate redis;
    @Mock private FeedRedisProperties props;
    @Mock private ZSetOperations<String, String> zsetOps;

    private FeedServiceImpl service;

    @BeforeEach
    void setUp() {
        FeedPostMapper feedPostMapper = Mappers.getMapper(FeedPostMapper.class);

        when(props.getKeyPrefix()).thenReturn(KEY_PREFIX);
        when(redis.opsForZSet()).thenReturn(zsetOps);

        service = new FeedServiceImpl(
                feedDbRepository,
                userServiceClient,
                postRepository,
                postCacheRepository,
                userCacheRepository,
                feedPostMapper,
                redis,
                props
        );
    }

    @Test
    @DisplayName("getFeed: maps posts using caches when Redis provides enough ids (no DB fallback)")
    void getFeed_shouldReturnMappedDtos_fromRedisAndCaches_only() {
        // redis ids
        when(zsetOps.reverseRange(eq(KEY_PREFIX + USER_ID), eq(0L), eq(2L)))
                .thenReturn(new LinkedHashSet<>(List.of("101", "102", "103")));

        List<Long> ids = List.of(101L, 102L, 103L);

        // post-cache hit
        Map<Long, PostCacheDto> postCache = Map.of(
                101L, postCache(101L, AUTHOR_ID_1, T2),
                102L, postCache(102L, AUTHOR_ID_2, T1),
                103L, postCache(103L, AUTHOR_ID_1, T1)
        );
        when(postCacheRepository.findAllByIds(eq(ids))).thenReturn(postCache);

        // user cache miss -> user-service fetch (order can vary)
        when(userCacheRepository.findAllByIds(anyList())).thenReturn(Map.of());
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(List.of(user(AUTHOR_ID_1, "u1"), user(AUTHOR_ID_2, "u2")));

        List<FeedPostResponseDto> out = service.getFeed(USER_ID, null, 3);

        assertEquals(3, out.size());
        assertEquals(ids, out.stream().map(FeedPostResponseDto::id).toList());

        assertEquals(AUTHOR_ID_1, out.get(0).author().id());
        assertEquals("u1", out.get(0).author().username());
        assertEquals(AUTHOR_ID_2, out.get(1).author().id());
        assertEquals("u2", out.get(1).author().username());

        verify(feedDbRepository, never()).findFeedPostsFirstPage(anyList(), any());
        verify(feedDbRepository, never()).findFeedPostsAfterCursor(anyList(), any(), any(), any());
        verify(postRepository, never()).findAllByIdIn(anyList());

        verify(userCacheRepository).findAllByIds(argThat(idsArg ->
                idsArg != null && idsArg.size() == 2 && idsArg.containsAll(List.of(AUTHOR_ID_1, AUTHOR_ID_2))
        ));
        verify(userServiceClient).getUsersByIds(argThat(idsArg ->
                idsArg != null && idsArg.size() == 2 && idsArg.containsAll(List.of(AUTHOR_ID_1, AUTHOR_ID_2))
        ));
    }

    @Test
    @DisplayName("getFeed: fallbacks to DB when Redis has fewer ids than limit and merges ids in order")
    void getFeed_shouldFallbackToDb_andMergeIds() {
        when(zsetOps.reverseRange(eq(KEY_PREFIX + USER_ID), eq(0L), eq(2L)))
                .thenReturn(new LinkedHashSet<>(List.of("101")));

        // cursor resolved from last redis id via cache
        PostCacheDto cursorPost = postCache(101L, AUTHOR_ID_1, T2);
        when(postCacheRepository.findAllByIds(eq(List.of(101L))))
                .thenReturn(Map.of(101L, cursorPost));

        // followees for user 10
        when(userServiceClient.getFollowees(eq(USER_ID), isNull(), isNull(), eq(0), eq(Integer.MAX_VALUE)))
                .thenReturn(List.of(user(201L, "f1"), user(202L, "f2")));

        Post db1 = Post.builder().id(102L).createdAt(T1).build();
        Post db2 = Post.builder().id(103L).createdAt(T1.minusMinutes(1)).build();

        // IMPORTANT: your FeedDbRepository has "findFeedPostsAfterCursor"
        when(feedDbRepository.findFeedPostsAfterCursor(
                eq(List.of(201L, 202L)),
                eq(T2),
                eq(101L),
                any(PageRequest.class)
        )).thenReturn(List.of(db1, db2));

        List<Long> finalIds = List.of(101L, 102L, 103L);

        when(postCacheRepository.findAllByIds(eq(finalIds)))
                .thenReturn(Map.of(
                        101L, postCache(101L, AUTHOR_ID_1, T2),
                        102L, postCache(102L, AUTHOR_ID_2, T1),
                        103L, postCache(103L, AUTHOR_ID_1, T1)
                ));

        when(userCacheRepository.findAllByIds(anyList())).thenReturn(Map.of());
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(List.of(user(AUTHOR_ID_1, "u1"), user(AUTHOR_ID_2, "u2")));

        List<FeedPostResponseDto> out = service.getFeed(USER_ID, null, 3);

        assertEquals(3, out.size());
        assertEquals(finalIds, out.stream().map(FeedPostResponseDto::id).toList());

        verify(feedDbRepository).findFeedPostsAfterCursor(
                eq(List.of(201L, 202L)),
                eq(T2),
                eq(101L),
                argThat(pr -> pr.getPageNumber() == 0 && pr.getPageSize() == 2)
        );

        verify(userCacheRepository).findAllByIds(argThat(idsArg ->
                idsArg != null && idsArg.size() == 2 && idsArg.containsAll(List.of(AUTHOR_ID_1, AUTHOR_ID_2))
        ));
        verify(userServiceClient).getUsersByIds(argThat(idsArg ->
                idsArg != null && idsArg.size() == 2 && idsArg.containsAll(List.of(AUTHOR_ID_1, AUTHOR_ID_2))
        ));
    }

    @Test
    @DisplayName("getFeed: if afterPostId provided but cursor cannot be resolved -> do not return newest DB posts")
    void getFeed_shouldNotFallback_whenAfterProvidedButCursorMissing() {
        long afterPostId = 999L;

        when(zsetOps.score(eq(KEY_PREFIX + USER_ID), eq(String.valueOf(afterPostId))))
                .thenReturn(null);

        when(postCacheRepository.findAllByIds(eq(List.of(afterPostId)))).thenReturn(Map.of());
        when(postRepository.findById(eq(afterPostId))).thenReturn(Optional.empty());

        List<FeedPostResponseDto> out = service.getFeed(USER_ID, afterPostId, 10);

        assertNotNull(out);
        assertTrue(out.isEmpty());

        verify(feedDbRepository, never()).findFeedPostsFirstPage(anyList(), any());
        verify(feedDbRepository, never()).findFeedPostsAfterCursor(anyList(), any(), any(), any(PageRequest.class));
        verify(userServiceClient, never()).getUsersByIds(anyList());
        // userCacheRepository may or may not be touched depending on implementation details
    }

    // fixtures
    private static PostCacheDto postCache(long id, long authorId, LocalDateTime createdAt) {
        return PostCacheDto.builder()
                .id(id)
                .authorId(authorId)
                .projectId(null)
                .content("c" + id)
                .published(true)
                .deleted(false)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .publishedAt(null)
                .scheduledAt(null)
                .build();
    }

    private static UserDto user(long id, String username) {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(username + "@mail.test")
                .build();
    }
}
