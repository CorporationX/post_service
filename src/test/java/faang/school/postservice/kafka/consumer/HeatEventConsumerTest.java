package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.model.CacheableNewsFeed;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.service.NewsFeedHeater;
import faang.school.postservice.cache.service.NewsFeedService;
import faang.school.postservice.cache.service.CacheableUserService;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HeatEventConsumerTest {
    @InjectMocks
    private HeatEventConsumer heatEventConsumer;
    @Mock
    private NewsFeedHeater newsFeedHeater;
    @Mock
    private NewsFeedService newsFeedService;
    @Mock
    private CacheableUserService cacheableUserService;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void testConsumeHeaterUsersEvent() {
        CacheableUser firstUser = new CacheableUser(1L, "username");
        CacheableUser secondUser = new CacheableUser(2L, "username");
        HeaterUsersEvent event = new HeaterUsersEvent(List.of(firstUser, secondUser));

        heatEventConsumer.consume(event, acknowledgment);

        verify(cacheableUserService, times(1)).saveAll(event.getUsers());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeHeaterNewsFeedEvent() {
        Long firstFollowerId = 1L;
        List<Long> firstFeedPostIds = List.of(43L, 37L, 22L);
        CacheableNewsFeed firstNewsFeed = new CacheableNewsFeed(firstFollowerId, firstFeedPostIds);
        Long secondFollowerId = 2L;
        List<Long> secondFeedPostIds = List.of(105L, 30L, 22L);
        CacheableNewsFeed secondNewsFeed = new CacheableNewsFeed(secondFollowerId, secondFeedPostIds);
        HeaterNewsFeedEvent event = new HeaterNewsFeedEvent(List.of(firstNewsFeed, secondNewsFeed));

        heatEventConsumer.consume(event, acknowledgment);

        verify(newsFeedService, times(1)).saveAllNewsFeeds(event.getNewsFeeds());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeHeaterPostsEvent() {
        List<Long> postIds = List.of(23L, 42L, 12L);
        HeaterPostsEvent event = new HeaterPostsEvent(postIds);

        heatEventConsumer.consume(event, acknowledgment);

        verify(newsFeedHeater, times(1)).saveAllPosts(postIds);
        verify(acknowledgment, times(1)).acknowledge();
    }
}