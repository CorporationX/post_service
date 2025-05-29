package faang.school.postservice.service;

import faang.school.postservice.client.FeignUserClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.TotalLikesIsZeroException;
import faang.school.postservice.service.redis.RedisCommentLikeCacheService;
import faang.school.postservice.service.redis.RedisPostLikeCacheService;
import faang.school.postservice.service.redis.UserCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private RedisPostLikeCacheService postLikeCache;

    @Mock
    private RedisCommentLikeCacheService commentLikeCache;

    @Mock
    private UserCacheService userCache;

    @Mock
    private FeignUserClient userFeignClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Pageable pageable;
    private Long entityId;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        entityId = 1L;
    }

    @Nested
    @DisplayName("findLikesByPostId Tests")
    class FindLikesByPostIdTests {
        @Test
        @DisplayName("When postId is null, should return empty page")
        void testFindLikesByPostId_nullPostId_returnsEmptyPage() {
            Page<UserDto> result = likeService.findLikesByPostId(null, pageable);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
            verifyNoInteractions(postLikeCache, commentLikeCache, userCache, userFeignClient);
        }

        @Test
        @DisplayName("When postId is valid, should call findLikesInternal with postLikeCache")
        void testFindLikesByPostId_validPostId_callsInternal() {
            when(postLikeCache.getEntityName())
                    .thenReturn("post");
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(0L);

            assertThrows(TotalLikesIsZeroException.class,
                    () -> likeService.findLikesByPostId(entityId, pageable));
            verify(postLikeCache).touchKeys(entityId);
        }
    }

    @Nested
    @DisplayName("findLikesByCommentId Tests")
    class FindLikesByCommentIdTests {
        @Test
        @DisplayName("When commentId is null, should return empty page")
        void testFindLikesByCommentId_nullCommentId_returnsEmptyPage() {
            Page<UserDto> result = likeService.findLikesByCommentId(null, pageable);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
            verifyNoInteractions(postLikeCache, commentLikeCache, userCache, userFeignClient);
        }

        @Test
        @DisplayName("When commentId is valid, should call findLikesInternal with commentLikeCache")
        void testFindLikesByCommentId_validCommentId_callsInternal() {
            when(commentLikeCache.getEntityName())
                    .thenReturn("comment");
            when(commentLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(0L);

            assertThrows(TotalLikesIsZeroException.class,
                    () -> likeService.findLikesByCommentId(entityId, pageable));
            verify(commentLikeCache).touchKeys(entityId);
        }
    }

    @Nested
    @DisplayName("findLikesInternal Tests")
    class FindLikesInternalTests {
        private List<Long> userIdsPage1;
        private List<UserDto> userDtosPage1;

        @BeforeEach
        void internalSetup() {
            userIdsPage1 = LongStream.rangeClosed(1, 10).boxed().toList();
            userDtosPage1 = userIdsPage1.stream().map(id ->
                    new UserDto(id, "User %d".formatted(id), "user%d@mail.com".formatted(id))).toList();
            when(postLikeCache.getEntityName())
                    .thenReturn("post");
        }

        @Test
        @DisplayName("L1 Cache Hit for User IDs, All Users in L2 Cache")
        void testFindLikesInternal_l1Hit_allUsersInL2() {
            long totalLikes = 25L;
            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(userIdsPage1);
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikes);
            when(userCache.bulkGet(userIdsPage1, 0, userIdsPage1.size()))
                    .thenReturn(userDtosPage1);

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertEquals(10, result.getContent().size());
            assertEquals(totalLikes, result.getTotalElements());
            assertFalse(result.isEmpty());
            verify(postLikeCache).touchKeys(entityId);
            verify(userFeignClient, never()).fetchUserDtosViaFeign(anyList(), anyString(), anyLong());
            verify(userCache, never()).bulkPut(any());
        }

        @Test
        @DisplayName("L1 Cache Miss, DB Fallback Populates L1, All Users in L2 Cache")
        void testFindLikesInternal_l1Miss_dbFallback_allUsersInL2() {
            long totalLikes = 15L;
            Pageable dbPageable = PageRequest.of(0, 10 * 5,
                    Sort.by("createdAt").descending());
            List<LikeDto> likesFromDb = userIdsPage1.stream()
                    .map(userId -> new LikeDto(userId, entityId, null))
                    .collect(Collectors.toList());
            Page<LikeDto> dbPage = new PageImpl<>(likesFromDb, dbPageable, totalLikes);

            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(null)
                    .thenReturn(userIdsPage1);
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikes);
            when(postLikeCache.fetchLikesPageFromDb(entityId, dbPageable))
                    .thenReturn(dbPage);
            when(userCache.bulkGet(userIdsPage1, 0, userIdsPage1.size()))
                    .thenReturn(userDtosPage1);

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertEquals(10, result.getContent().size());
            assertEquals(totalLikes, result.getTotalElements());
            verify(postLikeCache).populateLikesCacheFromDb(entityId, likesFromDb, totalLikes);
            verify(userFeignClient, never()).fetchUserDtosViaFeign(anyList(), anyString(), anyLong());
        }

        @Test
        @DisplayName("TotalLikesIsZeroException when total likes is 0")
        void testFindLikesInternal_totalLikesZero_throwsException() {
            when(postLikeCache.getUserIdsPage(entityId, 0, 10)).thenReturn(null);
            when(postLikeCache.getTotalLikesCount(entityId)).thenReturn(0L);

            TotalLikesIsZeroException ex = assertThrows(TotalLikesIsZeroException.class,
                    () -> likeService.findLikesInternal(entityId, pageable, postLikeCache));
            assertEquals("Total likes for %s with ID %d is zero.".formatted("post", entityId),
                    ex.getMessage());
        }

        @Test
        @DisplayName("L1 Hit, L2 Cache Miss for Some Users, Feign Success")
        void testFindLikesInternal_l1Hit_l2Miss_feignSuccess() {
            long totalLikes = 12L;
            List<Long> userIdsForPage = Arrays.asList(1L, 2L, 3L);
            UserDto user1Dto = new UserDto(1L, "User1", "u1@mail.com");
            UserDto user2Dto = new UserDto(2L, "User2", "u2@mail.com");
            UserDto user3Dto = new UserDto(3L, "User3", "u3@mail.com");

            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(userIdsForPage);
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikes);
            when(userCache.bulkGet(userIdsForPage, 0, userIdsForPage.size()))
                    .thenReturn(List.of(user1Dto));

            List<Long> feignMisses = List.of(2L, 3L);
            List<UserDto> feignFetchedUsers = List.of(user2Dto, user3Dto);
            when(userFeignClient.fetchUserDtosViaFeign(feignMisses, "post", entityId))
                    .thenReturn(feignFetchedUsers);

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertEquals(3, result.getContent().size());
            assertTrue(result.getContent().containsAll(List.of(user1Dto, user2Dto, user3Dto)));
            assertEquals(totalLikes, result.getTotalElements());
            verify(userCache).bulkPut(argThat(users ->
                    users.size() == 2 && users.containsAll(feignFetchedUsers)));
        }

        @Test
        @DisplayName("L1 Hit, L2 Cache Miss, Feign Returns Partial Users")
        void testFindLikesInternal_l1Hit_l2Miss_feignPartial() {
            long totalLikes = 10L;
            List<Long> userIdsForPage = List.of(1L, 2L, 3L, 4L);
            UserDto user1Dto = new UserDto(1L, "User1", "u1@mail.com");
            UserDto user2Dto = new UserDto(2L, "User2", "u2@mail.com");

            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(userIdsForPage);
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikes);

            when(userCache.bulkGet(userIdsForPage, 0, userIdsForPage.size()))
                    .thenReturn(List.of(user1Dto));

            List<Long> feignMisses = Arrays.asList(2L, 3L, 4L);
            when(userFeignClient.fetchUserDtosViaFeign(feignMisses, "post", entityId))
                    .thenReturn(List.of(user2Dto));

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().containsAll(Arrays.asList(user1Dto, user2Dto)));
            assertEquals(totalLikes, result.getTotalElements());
            verify(userCache).bulkPut(argThat(users ->
                    users.size() == 1 && users.contains(user2Dto)));
        }


        @Test
        @DisplayName("L1 Hit, L2 Cache Miss, Feign Fails (Exception)")
        void testFindLikesInternal_l1Hit_l2Miss_feignException() {
            long totalLikes = 7L;
            List<Long> userIdsForPage = Arrays.asList(1L, 2L, 3L);
            UserDto user1Dto = new UserDto(1L, "User1", "u1@mail.com");
            List<Long> feignMisses = Arrays.asList(2L, 3L);

            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(userIdsForPage);
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikes);
            when(userCache.bulkGet(userIdsForPage, 0, userIdsForPage.size()))
                    .thenReturn(List.of(user1Dto));
            when(userFeignClient.fetchUserDtosViaFeign(feignMisses, "post", entityId))
                    .thenReturn(Collections.emptyList());

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertEquals(1, result.getContent().size());
            assertTrue(result.getContent().contains(user1Dto));
            assertEquals(1, result.getTotalElements());
            verify(userCache, never()).bulkPut(any());
        }

        @Test
        @DisplayName("DB Fallback finds no likes, but total > 0 (e.g. put total count in cache)")
        void testFindLikesInternal_l1Miss_dbFallback_noLikesInDb_totalGreaterThanZero() {
            long totalLikesStale = 5L;
            Pageable dbPageable = PageRequest.of(0, 10 * 5, Sort.by("createdAt").descending());
            Page<LikeDto> emptyDbPage = new PageImpl<>(Collections.emptyList(), dbPageable, 0);

            when(postLikeCache.getUserIdsPage(entityId, 0, 10))
                    .thenReturn(null)
                    .thenReturn(Collections.emptyList());
            when(postLikeCache.getTotalLikesCount(entityId))
                    .thenReturn(totalLikesStale);
            when(postLikeCache.fetchLikesPageFromDb(entityId, dbPageable))
                    .thenReturn(emptyDbPage);

            Page<UserDto> result = likeService.findLikesInternal(entityId, pageable, postLikeCache);

            assertTrue(result.getContent().isEmpty());
            assertEquals(totalLikesStale, result.getTotalElements());
            verify(postLikeCache).populateLikesCacheFromDb(entityId, Collections.emptyList(), totalLikesStale);
        }
    }
}
