package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.dto.redis.CachedFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.CacheFeedRepository;
import faang.school.postservice.service.feed.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceImplTest {

    @InjectMocks
    private FeedServiceImpl feedService;

    @Mock
    private CacheFeedRepository cacheFeedRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private Long anyLong;
    private PostEvent anyPostEvent;
    private CachedFeedDto anyCachedFeedDto;

    @BeforeEach
    public void setUp() {
        anyLong = 1L;
        anyPostEvent = new PostEvent(anyLong, List.of(anyLong));
        anyCachedFeedDto = new CachedFeedDto(anyLong, new LinkedHashSet<>(List.of(anyLong)));
    }

    @Test
    public void updateFeed_NonexistentFollower() {
        when(userServiceClient.getUser(anyLong)).thenThrow(new RuntimeException());

        feedService.updateFeeds(anyPostEvent);

        verify(cacheFeedRepository).findById(anyLong);
        verify(userServiceClient).getUser(anyLong);
        verify(cacheFeedRepository, never()).save(any(CachedFeedDto.class));
    }

    @Test
    public void updateFeed_SuccessfullyUpdatesNonexistentFeed() {
        String anyString = "anyString";

        when(userServiceClient.getUser(anyLong)).thenReturn(new UserDto(anyLong, anyString, anyString));

        feedService.updateFeeds(anyPostEvent);

        verify(cacheFeedRepository).findById(anyLong);
        verify(userServiceClient).getUser(anyLong);
        verify(cacheFeedRepository).save(any(CachedFeedDto.class));
    }

    @Test
    public void updateFeed_SuccessfullyUpdatesUnlockedFeed() {
        when(cacheFeedRepository.findById(anyLong)).thenReturn(Optional.of(anyCachedFeedDto));

        feedService.updateFeeds(anyPostEvent);

        verify(cacheFeedRepository).findById(anyLong);
        verify(userServiceClient, never()).getUser(anyLong);
        verify(cacheFeedRepository, times(1)).save(any(CachedFeedDto.class));
    }

    @Test
    public void updateFeed_SuccessfullyUpdatesUnlockedFeed_LessPostsThenMaxFeedSize()
            throws NoSuchFieldException, IllegalAccessException {
        int anyIntMoreThanFeedSize = 1000;
        Field maxFeedSize = FeedServiceImpl.class.getDeclaredField("maxFeedSize");
        maxFeedSize.setAccessible(true);
        maxFeedSize.set(feedService, anyIntMoreThanFeedSize);

        when(cacheFeedRepository.findById(anyLong)).thenReturn(Optional.of(anyCachedFeedDto));

        feedService.updateFeeds(anyPostEvent);

        verify(cacheFeedRepository).findById(anyLong);
        verify(userServiceClient, never()).getUser(anyLong);
        verify(cacheFeedRepository, times(1)).save(any(CachedFeedDto.class));
    }

    @Test
    public void updateFeed_SuccessfullyUpdatesLockedFeed() {
        when(cacheFeedRepository.findById(anyLong)).thenReturn(Optional.of(anyCachedFeedDto));
        when(cacheFeedRepository.save(anyCachedFeedDto))
                .thenThrow(new OptimisticLockingFailureException("anyMessage")).thenReturn(any(CachedFeedDto.class));

        feedService.updateFeeds(anyPostEvent);

        verify(cacheFeedRepository).findById(anyLong);
        verify(userServiceClient, never()).getUser(anyLong);
        verify(cacheFeedRepository, times(2)).save(any(CachedFeedDto.class));
    }
}
