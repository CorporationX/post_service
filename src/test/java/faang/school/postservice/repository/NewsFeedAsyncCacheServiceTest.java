package faang.school.postservice.repository;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import faang.school.postservice.service.cache.NewsFeedAsyncCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedAsyncCacheServiceTest {

    @Mock
    private SortedSetCacheRepository<Long> sortedSetCacheRepository;

    @Spy
    private NewsFeedProperties newsFeedProperties;

    @InjectMocks
    private NewsFeedAsyncCacheService newsFeedAsyncCacheService;

    @Captor
    private ArgumentCaptor<Runnable> runnableArgumentCaptor;

    private Long userId;
    private String userKey;
    private Long postId;

    @BeforeEach
    void setUp() {
        userId = 123L;
        userKey = userId + "::news_feed";
        postId = 42L;

        newsFeedProperties.setNewsFeedSize(3);
    }

    @Test
    void save_shouldAddPostToCache() {
        when(sortedSetCacheRepository.size(userKey)).thenReturn(4L);

        newsFeedAsyncCacheService.save(userId, postId);

        verify(sortedSetCacheRepository).executeInOptimisticLock(runnableArgumentCaptor.capture(), eq(userKey));
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheRepository).put(eq(userKey), eq(postId), anyDouble());
        verify(sortedSetCacheRepository).popMin(userKey);
    }

    @Test
    void save_shouldRemoveOldestPostWhenSizeExceedsLimit() {
        when(sortedSetCacheRepository.size(userKey)).thenReturn(0L);

        newsFeedAsyncCacheService.save(userId, postId);

        verify(sortedSetCacheRepository).executeInOptimisticLock(runnableArgumentCaptor.capture(), eq(userKey));
        runnableArgumentCaptor.getValue().run();
        verify(sortedSetCacheRepository).put(eq(userKey), eq(postId), anyDouble());
        verify(sortedSetCacheRepository, never()).popMin(userKey);
    }

    @Test
    void getRange() {
        int count = 5, offset = 0;
        String stringPostId = Long.toString(postId);
        List<Long> correctResult = List.of(1L, 2L, 3L, 4L, 5L);
        when(sortedSetCacheRepository.getRange(userKey, stringPostId, 0, count, Long.class)).thenReturn(correctResult);

        CompletableFuture<List<Long>> futureResult = newsFeedAsyncCacheService.getRange(userId, postId, count);
        List<Long> result = futureResult.join();

        verify(sortedSetCacheRepository).getRange(userKey, stringPostId, offset, count, Long.class);
        assertNotNull(result);
        assertEquals(correctResult, result);
    }
}
