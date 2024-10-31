package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableNewsFeed;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheableNewsFeedRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.kafka.KafkaTopicProperties;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = NewsFeedService.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NewsFeedServiceTest {
    @Autowired
    @SpyBean
    private NewsFeedService newsFeedService;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private PostService postService;
    @MockBean
    private CacheablePostService cacheablePostService;
    @MockBean
    private CacheableNewsFeedRepository cacheableNewsFeedRepository;
    @MockBean
    private RedisConcurrentExecutor concurrentExecutor;
    @SpyBean
    private KafkaTopicProperties kafkaTopicProperties;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Value("${news-feed.batch-size}")
    private int batchSize;
    @Value("${news-feed.max-size}")
    private int newsFeedMaxSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;

    private Long userId;
    private Long postId;
    private String key;
    private List<Long> postIds;
    private Long lastPostId;
    private List<Long> postIdsWithLastPostId;
    private List<Long> postIdsWithoutLastPostId;
    private List<CacheablePost> cacheablePostsWithLastPostId;
    private List<CacheablePost> cacheablePostsWithoutLastPostId;
    private List<Long> followeeIds;
    private UserDto userDto;
    private TreeSet<CacheablePost> result;
    private List<Long> notEnoughPostIds;
    private List<Long> extraPostIds;
    private List<CacheablePost> notEnoughPosts;
    private List<CacheablePost> extraPosts;

    @BeforeEach
    void setUp() {
        userId = 1L;
        postId = 1L;
        key = newsFeedPrefix + userId;
        postIds = new ArrayList<>(Arrays.asList(
                20L, 19L, 18L, 17L, 16L, 15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L
        ));
        lastPostId = 12L;
        followeeIds = List.of(20L, 18L, 10L);
        userDto = UserDto.builder()
                .id(userId)
                .followeesIds(followeeIds)
                .build();
        result = new TreeSet<>();

        cacheablePostsWithoutLastPostId = new ArrayList<>();
        postIdsWithoutLastPostId = postIds.subList(0, batchSize);
        postIdsWithoutLastPostId
                .forEach(postId -> cacheablePostsWithoutLastPostId.add(CacheablePost.builder().id(postId).build()));

        int startIndex = postIds.indexOf(lastPostId) + 1;
        cacheablePostsWithLastPostId = new ArrayList<>();
        postIdsWithLastPostId = postIds.subList(startIndex, startIndex + batchSize);
        postIdsWithLastPostId
                .forEach(postId -> cacheablePostsWithLastPostId.add(CacheablePost.builder().id(postId).build()));


        notEnoughPostIds = new ArrayList<>(Arrays.asList(7L, 6L, 5L));
        notEnoughPosts = new ArrayList<>();
        notEnoughPostIds.forEach(postId -> notEnoughPosts.add(CacheablePost.builder().id(postId).build()));

        extraPostIds = new ArrayList<>(Arrays.asList(4L, 3L));
        extraPosts = new ArrayList<>();
        extraPostIds.forEach(postId -> extraPosts.add(CacheablePost.builder().id(postId).build()));
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsEnoughInCache() {
        result.addAll(cacheablePostsWithoutLastPostId);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(cacheablePostService.getAllByIds(postIdsWithoutLastPostId)).thenReturn(cacheablePostsWithoutLastPostId);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, null);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService, times(1)).getAllByIds(postIdsWithoutLastPostId);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsEnoughInCache() {
        result.addAll(cacheablePostsWithLastPostId);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(cacheablePostService.getAllByIds(postIdsWithLastPostId)).thenReturn(cacheablePostsWithLastPostId);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService, times(1)).getAllByIds(postIdsWithLastPostId);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenNoPostsInCache() {
        result.addAll(cacheablePostsWithoutLastPostId);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthors(followeeIds, batchSize)).thenReturn(cacheablePostsWithoutLastPostId);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, null);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(1)).findByAuthors(followeeIds, batchSize);
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService,times(1)).setCommentsFromDB(cacheablePostsWithoutLastPostId);
        verify(cacheablePostService, times(1)).setAuthors(result);
        verify(cacheablePostService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenNoPostsInCache() {
        result.addAll(cacheablePostsWithLastPostId);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostId, batchSize)).thenReturn(cacheablePostsWithLastPostId);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1)).findByAuthorsBeforeId(followeeIds, lastPostId, batchSize);
        verify(cacheablePostService,times(1)).setCommentsFromDB(cacheablePostsWithLastPostId);
        verify(cacheablePostService, times(1)).setAuthors(result);
        verify(cacheablePostService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsNotFound() {
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthors(followeeIds, batchSize)).thenReturn(new ArrayList<>());

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, null);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(1)).findByAuthors(followeeIds, batchSize);
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService,times(0)).setCommentsFromDB(anyList());
        verify(cacheablePostService, times(0)).setAuthors(any());
        verify(cacheablePostService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(new TreeSet<>(), actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsNotFound() {
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostId, batchSize)).thenReturn(new ArrayList<>());

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1)).findByAuthorsBeforeId(followeeIds, lastPostId, batchSize);
        verify(cacheablePostService,times(0)).setCommentsFromDB(anyList());
        verify(cacheablePostService, times(0)).setAuthors(any());
        verify(cacheablePostService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(new TreeSet<>(), actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenSomePostsInCacheExpired() {
        List<Long> expiredPostIds = new ArrayList<>();
        expiredPostIds.add(postIdsWithoutLastPostId.get(batchSize - 2));
        expiredPostIds.add(postIdsWithoutLastPostId.get(batchSize - 1));
        List<CacheablePost> expiredPosts = new ArrayList<>();
        expiredPostIds.forEach(postId -> expiredPosts.add(CacheablePost.builder().id(postId).build()));

        List<Long> notExpiredPostIds = new ArrayList<>(postIdsWithoutLastPostId);
        notExpiredPostIds.removeAll(expiredPostIds);
        List<CacheablePost> notExpiredPosts = new ArrayList<>();
        notExpiredPostIds.forEach(postId -> notExpiredPosts.add(CacheablePost.builder().id(postId).build()));

        result = new TreeSet<>();
        result.addAll(expiredPosts);
        result.addAll(notExpiredPosts);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(cacheablePostService.getAllByIds(postIdsWithoutLastPostId)).thenReturn(notExpiredPosts);
        when(postService.findAllByIdsWithLikes(expiredPostIds)).thenReturn(expiredPosts);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, null);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(cacheablePostService, times(1)).getAllByIds(postIdsWithoutLastPostId);
        verify(postService, times(1)).findAllByIdsWithLikes(expiredPostIds);
        verify(cacheablePostService,times(1)).setCommentsFromDB(expiredPosts);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenSomePostsInCacheExpired() {
        List<Long> expiredPostIds = new ArrayList<>();
        expiredPostIds.add(postIdsWithLastPostId.get(batchSize - 2));
        expiredPostIds.add(postIdsWithLastPostId.get(batchSize - 1));
        List<CacheablePost> expiredPosts = new ArrayList<>();
        expiredPostIds.forEach(postId -> expiredPosts.add(CacheablePost.builder().id(postId).build()));

        List<Long> notExpiredPostIds = new ArrayList<>(postIdsWithLastPostId);
        notExpiredPostIds.removeAll(expiredPostIds);
        List<CacheablePost> notExpiredPosts = new ArrayList<>();
        notExpiredPostIds.forEach(postId -> notExpiredPosts.add(CacheablePost.builder().id(postId).build()));

        result = new TreeSet<>();
        result.addAll(expiredPosts);
        result.addAll(notExpiredPosts);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(cacheablePostService.getAllByIds(postIdsWithLastPostId)).thenReturn(notExpiredPosts);
        when(postService.findAllByIdsWithLikes(expiredPostIds)).thenReturn(expiredPosts);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(cacheablePostService, times(1)).getAllByIds(postIdsWithLastPostId);
        verify(postService, times(1)).findAllByIdsWithLikes(expiredPostIds);
        verify(cacheablePostService,times(1)).setCommentsFromDB(expiredPosts);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsNotEnoughInCache() {
        result = new TreeSet<>();
        result.addAll(notEnoughPosts);
        Long lastPostIdForBD = result.last().getId();
        result.addAll(extraPosts);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(notEnoughPostIds);
        when(cacheablePostService.getAllByIds(notEnoughPostIds)).thenReturn(notEnoughPosts);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostIdForBD, extraPostIds.size())).thenReturn(extraPosts);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, null);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(cacheablePostService, times(1)).getAllByIds(notEnoughPostIds);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1))
                .findByAuthorsBeforeId(followeeIds, lastPostIdForBD, extraPosts.size());
        verify(cacheablePostService, times(1)).setCommentsFromDB(extraPosts);
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsNotEnoughInCache() {
        lastPostId = 8L;

        result = new TreeSet<>();
        result.addAll(notEnoughPosts);
        Long lastPostIdForDB = result.last().getId();
        result.addAll(extraPosts);
        when(cacheableNewsFeedRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(cacheablePostService.getAllByIds(notEnoughPostIds)).thenReturn(notEnoughPosts);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostIdForDB, extraPostIds.size())).thenReturn(extraPosts);

        TreeSet<CacheablePost> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(cacheableNewsFeedRepository, times(1)).getSortedPostIds(key);
        verify(cacheablePostService, times(1)).getAllByIds(notEnoughPostIds);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1))
                .findByAuthorsBeforeId(followeeIds, lastPostIdForDB, extraPosts.size());
        verify(cacheablePostService, times(1)).setCommentsFromDB(extraPosts);
        verify(cacheablePostService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testAddPostConcurrent() {
        doNothing().when(newsFeedService).addPost(userId, postId);

        newsFeedService.addPostConcurrent(userId, postId);

        verify(concurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(newsFeedService, times(1)).addPost(userId, postId);
    }

    @Test
    void testAddPost() {
        when(cacheableNewsFeedRepository.getSize(key)).thenReturn(newsFeedMaxSize - 10L);

        newsFeedService.addPost(userId, postId);

        verify(cacheableNewsFeedRepository, times(1)).addPostId(key, postId);
        verify(cacheableNewsFeedRepository, times(1)).getSize(key);
        verify(cacheableNewsFeedRepository, times(0)).removeLastPostId(key);
    }

    @Test
    void testAddPostWhenFeedSizeIsMax() {
        when(cacheableNewsFeedRepository.getSize(key))
                .thenReturn((long) newsFeedMaxSize + 1)
                .thenReturn((long) newsFeedMaxSize);

        newsFeedService.addPost(userId, postId);

        verify(cacheableNewsFeedRepository, times(1)).addPostId(key, postId);
        verify(cacheableNewsFeedRepository, times(2)).getSize(key);
        verify(cacheableNewsFeedRepository, times(1)).removeLastPostId(key);
    }

    @Test
    void testAddPostWhenFeedSizeIsMoreMaxSize() {
        when(cacheableNewsFeedRepository.getSize(key))
                .thenReturn((long) newsFeedMaxSize + 4)
                .thenReturn((long) newsFeedMaxSize + 3)
                .thenReturn((long) newsFeedMaxSize + 2)
                .thenReturn((long) newsFeedMaxSize + 1)
                .thenReturn((long) newsFeedMaxSize);

        newsFeedService.addPost(userId, postId);

        verify(cacheableNewsFeedRepository, times(1)).addPostId(key, postId);
        verify(cacheableNewsFeedRepository, times(5)).getSize(key);
        verify(cacheableNewsFeedRepository, times(4)).removeLastPostId(key);
    }

    @Test
    void testSaveAllNewsFeeds() {
        CacheableNewsFeed firstFeed = new CacheableNewsFeed(2L, List.of(1L, 2L, 3L));
        CacheableNewsFeed secondFeed = new CacheableNewsFeed(2L, List.of(4L, 5L, 6L));
        CacheableNewsFeed thirdFeed = new CacheableNewsFeed(2L, List.of(7L, 8L, 9L));
        List<CacheableNewsFeed> newsFeeds = List.of(firstFeed, secondFeed, thirdFeed);

        newsFeedService.saveAllNewsFeeds(newsFeeds);

        verify(cacheableNewsFeedRepository, times(1))
                .addAll(newsFeedPrefix + firstFeed.getFollowerId(), firstFeed.getPostIds());
        verify(cacheableNewsFeedRepository, times(1))
                .addAll(newsFeedPrefix + secondFeed.getFollowerId(), secondFeed.getPostIds());
        verify(cacheableNewsFeedRepository, times(1))
                .addAll(newsFeedPrefix + thirdFeed.getFollowerId(), thirdFeed.getPostIds());
    }

    @Test
    void testGetNewsFeedsForUsers() {
        CacheableUser firstUser = new CacheableUser(1L, "username");
        CacheableUser secondUser = new CacheableUser(2L, "username");
        CacheableUser thirdUser = new CacheableUser(3L, "username");
        List<CacheableUser> cacheableUsers = List.of(firstUser, secondUser, thirdUser);

        List<Long> firstUserPostIds = List.of(1L, 2L, 3L);
        List<Long> secondUserPostIds = List.of(4L, 5L, 6L);
        List<Long> thirdUserPostIds = new ArrayList<>();

        List<CacheableNewsFeed> expected = List.of(
                new CacheableNewsFeed(firstUser.getId(), firstUserPostIds),
                new CacheableNewsFeed(secondUser.getId(), secondUserPostIds)
        );

        when(postService.findPostIdsByFollowerId(firstUser.getId(), newsFeedMaxSize)).thenReturn(firstUserPostIds);
        when(postService.findPostIdsByFollowerId(secondUser.getId(), newsFeedMaxSize)).thenReturn(secondUserPostIds);
        when(postService.findPostIdsByFollowerId(thirdUser.getId(), newsFeedMaxSize)).thenReturn(thirdUserPostIds);

        List<CacheableNewsFeed> actual = newsFeedService.getNewsFeedsForUsers(cacheableUsers);

        verify(postService, times(1)).findPostIdsByFollowerId(firstUser.getId(), newsFeedMaxSize);
        verify(postService, times(1)).findPostIdsByFollowerId(secondUser.getId(), newsFeedMaxSize);
        verify(postService, times(1)).findPostIdsByFollowerId(thirdUser.getId(), newsFeedMaxSize);
        assertEquals(expected, actual);
    }
}