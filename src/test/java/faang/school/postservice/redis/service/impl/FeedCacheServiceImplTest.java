package faang.school.postservice.redis.service.impl;

import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedCacheServiceImplTest {

    @Mock
    RedissonClient redissonClient;

    @Mock
    FeedsCacheRepository feedsCacheRepository;

    @Mock
    RLock lock;

    @InjectMocks
    FeedCacheServiceImpl feedCacheService;

    @Captor
    ArgumentCaptor<FeedCache> feedCacheArgumentCaptor = ArgumentCaptor.forClass(FeedCache.class);

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);


    private FeedCache feedCache;
    private Long feedId;
    private Long postId;

    @BeforeEach
    public void setUp() {
        feedId = 1L;
        postId = 3L;
        feedCache = FeedCache.builder()
                .id(feedId)
                .postIds(new LinkedList<>(Arrays.asList(1L, 2L)))
                .build();
    }

    @Test
    public void testGetAndSaveFeedSuccess() {
        when(redissonClient.getLock("lock:" + feedId)).thenReturn(lock);
        when(feedsCacheRepository.findById(feedId)).thenReturn(Optional.ofNullable(feedCache));
        ReflectionTestUtils.setField(feedCacheService, "feedSize", 3);

        feedCacheService.getAndSaveFeed(feedId, postId);

        verify(feedsCacheRepository, times(1)).save(feedCacheArgumentCaptor.capture());
        verify(redissonClient, times(1)).getLock(stringArgumentCaptor.capture());

        FeedCache resultFeedCache = feedCacheArgumentCaptor.getValue();
        Assertions.assertAll(
                () -> assertEquals(feedId, resultFeedCache.getId()),
                () -> assertEquals(List.of(3L, 1L, 2L), resultFeedCache.getPostIds()),
                () -> assertEquals("lock:" + feedId, stringArgumentCaptor.getValue())
        );
    }

    @Test
    public void testGetAndSaveFeed_AddExistedId_ShouldReturnSameFeed() {
        Long postId = 2L;
        feedCache.setPostIds(new LinkedList<>(Arrays.asList(3L, 2L, 1L)));
        when(redissonClient.getLock("lock:" + feedId)).thenReturn(lock);
        when(feedsCacheRepository.findById(feedId)).thenReturn(Optional.ofNullable(feedCache));
        ReflectionTestUtils.setField(feedCacheService, "feedSize", 3);

        feedCacheService.getAndSaveFeed(feedId, postId);

        verify(feedsCacheRepository, times(1)).save(feedCacheArgumentCaptor.capture());
        verify(redissonClient, times(1)).getLock(stringArgumentCaptor.capture());

        FeedCache resultFeedCache = feedCacheArgumentCaptor.getValue();
        Assertions.assertAll(
                () -> assertEquals(feedId, resultFeedCache.getId()),
                () -> assertEquals(List.of(3L, 2L, 1L), resultFeedCache.getPostIds()),
                () -> assertEquals("lock:" + feedId, stringArgumentCaptor.getValue())
        );
    }
}