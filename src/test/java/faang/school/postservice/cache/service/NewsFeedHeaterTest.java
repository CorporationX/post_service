package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.NewsFeedRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.util.ListSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
    private PostRedisService postRedisService;
    @MockBean
    private NewsFeedService newsFeedService;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.topic.heater.users}")
    private String heaterUsersTopic;
    @Value("${spring.kafka.topic.heater.news-feeds}")
    private String heaterNewsFeedsTopic;
    @Value("${spring.kafka.topic.heater.posts}")
    private String heaterPostsTopic;

    List<UserRedis> usersRedis;
    List<List<UserRedis>> splitUsers;
    List<NewsFeedRedis> newsFeeds;
    List<List<NewsFeedRedis>> splitNewsFeeds;
    List<Long> postIds;
    List<List<Long>> splitPostIds;
    List<PostRedis> posts;
    @BeforeEach
    void setUp() {
        usersRedis = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            usersRedis.add(new UserRedis(i, "username" + i));
        }
        splitUsers = ListSplitter.split(usersRedis, batchSize);

        newsFeeds = new ArrayList<>();
        postIds = List.of(3L, 2L, 1L);
        usersRedis.forEach(user -> newsFeeds.add(new NewsFeedRedis(user.getId(), postIds)));
        splitNewsFeeds = ListSplitter.split(newsFeeds, batchSize);

        splitPostIds = ListSplitter.split(postIds, batchSize);

        posts = new ArrayList<>();
        postIds.forEach(id -> posts.add(PostRedis.builder().id(id).build()));
    }

    @Test
    void testHeatPartWithUsers() {
        when(userServiceClient.getActiveUsersRedis()).thenReturn(usersRedis);
        when(newsFeedService.getNewsFeedsForUsers(usersRedis)).thenReturn(newsFeeds);

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
        verify(newsFeedService, times(1)).setComments(posts);
        verify(postRedisService, times(1)).saveAll(posts);
    }
}