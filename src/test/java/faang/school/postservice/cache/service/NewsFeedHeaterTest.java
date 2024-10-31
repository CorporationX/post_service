package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableNewsFeed;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.KafkaTopicProperties;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.service.PostService;
import org.apache.commons.collections4.ListUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NewsFeedHeater.class)
@ExtendWith(MockitoExtension.class)
class NewsFeedHeaterTest {
    @Autowired
    private NewsFeedHeater newsFeedHeater;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private PostService postService;
    @MockBean
    private CacheablePostService cacheablePostService;
    @MockBean
    private NewsFeedService newsFeedService;
    @SpyBean
    private KafkaTopicProperties kafkaTopicProperties;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.topic.heater.users}")
    private String heaterUsersTopic;
    @Value("${spring.kafka.topic.heater.news-feeds}")
    private String heaterNewsFeedsTopic;
    @Value("${spring.kafka.topic.heater.posts}")
    private String heaterPostsTopic;

    List<CacheableUser> cacheableUsers;
    List<List<CacheableUser>> splitUsers;
    List<CacheableNewsFeed> newsFeeds;
    List<List<CacheableNewsFeed>> splitNewsFeeds;
    List<Long> postIds;
    List<List<Long>> splitPostIds;
    List<CacheablePost> posts;
    @BeforeEach
    void setUp() {
        cacheableUsers = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            cacheableUsers.add(new CacheableUser(i, "username" + i));
        }
        splitUsers = ListUtils.partition(cacheableUsers, batchSize);

        newsFeeds = new ArrayList<>();
        postIds = List.of(3L, 2L, 1L);
        cacheableUsers.forEach(user -> newsFeeds.add(new CacheableNewsFeed(user.getId(), postIds)));
        splitNewsFeeds = ListUtils.partition(newsFeeds, batchSize);

        splitPostIds = ListUtils.partition(postIds, batchSize);

        posts = new ArrayList<>();
        postIds.forEach(id -> posts.add(CacheablePost.builder().id(id).build()));
    }

    @Test
    void testHeat() {
        when(userServiceClient.getActiveCacheableUsers()).thenReturn(cacheableUsers);
        when(newsFeedService.getNewsFeedsForUsers(cacheableUsers)).thenReturn(newsFeeds);

        newsFeedHeater.heat();

        verify(kafkaProducer, times(splitUsers.size()))
                .send(eq(heaterUsersTopic), any(HeaterUsersEvent.class));
        verify(kafkaProducer, times(splitNewsFeeds.size()))
                .send(eq(heaterNewsFeedsTopic), any(HeaterNewsFeedEvent.class));
        verify(kafkaProducer, times(splitPostIds.size()))
                .send(eq(heaterPostsTopic), any(HeaterPostsEvent.class));
    }

    @Test
    void testSaveAllPosts() {
        when(postService.findAllByIdsWithLikes(postIds)).thenReturn(posts);

        newsFeedHeater.saveAllPosts(postIds);

        verify(postService, times(1)).findAllByIdsWithLikes(postIds);
        verify(cacheablePostService, times(1)).setCommentsFromDB(posts);
        verify(cacheablePostService, times(1)).saveAll(posts);
    }
}