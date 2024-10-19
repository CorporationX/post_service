package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.NewsFeedRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.NewsFeedRedisRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
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
    private PostRedisService postRedisService;
    @MockBean
    private NewsFeedRedisRepository newsFeedRedisRepository;
    @MockBean
    private RedisConcurrentExecutor concurrentExecutor;
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
    private List<PostRedis> postsRedisWithLastPostId;
    private List<PostRedis> postsRedisWithoutLastPostId;
    private List<Long> followeeIds;
    private UserDto userDto;
    private TreeSet<PostRedis> result;
    private List<Long> notEnoughPostIds;
    private List<Long> extraPostIds;
    private List<PostRedis> notEnoughPosts;
    private List<PostRedis> extraPosts;

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

        postsRedisWithoutLastPostId = new ArrayList<>();
        postIdsWithoutLastPostId = postIds.subList(0, batchSize);
        postIdsWithoutLastPostId
                .forEach(postId -> postsRedisWithoutLastPostId.add(PostRedis.builder().id(postId).build()));

        int startIndex = postIds.indexOf(lastPostId) + 1;
        postsRedisWithLastPostId = new ArrayList<>();
        postIdsWithLastPostId = postIds.subList(startIndex, startIndex + batchSize);
        postIdsWithLastPostId
                .forEach(postId -> postsRedisWithLastPostId.add(PostRedis.builder().id(postId).build()));


        notEnoughPostIds = new ArrayList<>(Arrays.asList(7L, 6L, 5L));
        notEnoughPosts = new ArrayList<>();
        notEnoughPostIds.forEach(postId -> notEnoughPosts.add(PostRedis.builder().id(postId).build()));

        extraPostIds = new ArrayList<>(Arrays.asList(4L, 3L));
        extraPosts = new ArrayList<>();
        extraPostIds.forEach(postId -> extraPosts.add(PostRedis.builder().id(postId).build()));
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsEnoughInCache() {
        result.addAll(postsRedisWithoutLastPostId);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(postRedisService.getAllByIds(postIdsWithoutLastPostId)).thenReturn(postsRedisWithoutLastPostId);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, null);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService, times(1)).getAllByIds(postIdsWithoutLastPostId);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postRedisService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsEnoughInCache() {
        result.addAll(postsRedisWithLastPostId);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(postRedisService.getAllByIds(postIdsWithLastPostId)).thenReturn(postsRedisWithLastPostId);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService, times(1)).getAllByIds(postIdsWithLastPostId);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postRedisService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenNoPostsInCache() {
        result.addAll(postsRedisWithoutLastPostId);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthors(followeeIds, batchSize)).thenReturn(postsRedisWithoutLastPostId);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, null);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(1)).findByAuthors(followeeIds, batchSize);
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService,times(1)).setCommentsFromDB(postsRedisWithoutLastPostId);
        verify(postRedisService, times(1)).setAuthors(result);
        verify(postRedisService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenNoPostsInCache() {
        result.addAll(postsRedisWithLastPostId);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostId, batchSize)).thenReturn(postsRedisWithLastPostId);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1)).findByAuthorsBeforeId(followeeIds, lastPostId, batchSize);
        verify(postRedisService,times(1)).setCommentsFromDB(postsRedisWithLastPostId);
        verify(postRedisService, times(1)).setAuthors(result);
        verify(postRedisService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsNotFound() {
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthors(followeeIds, batchSize)).thenReturn(new ArrayList<>());

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, null);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(1)).findByAuthors(followeeIds, batchSize);
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService,times(0)).setCommentsFromDB(anyList());
        verify(postRedisService, times(0)).setAuthors(any());
        verify(postRedisService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(new TreeSet<>(), actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsNotFound() {
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(new ArrayList<>());
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostId, batchSize)).thenReturn(new ArrayList<>());

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1)).findByAuthorsBeforeId(followeeIds, lastPostId, batchSize);
        verify(postRedisService,times(0)).setCommentsFromDB(anyList());
        verify(postRedisService, times(0)).setAuthors(any());
        verify(postRedisService, times(0)).getAllByIds(anyList());
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        assertEquals(new TreeSet<>(), actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenSomePostsInCacheExpired() {
        List<Long> expiredPostIds = new ArrayList<>();
        expiredPostIds.add(postIdsWithoutLastPostId.get(batchSize - 2));
        expiredPostIds.add(postIdsWithoutLastPostId.get(batchSize - 1));
        List<PostRedis> expiredPosts = new ArrayList<>();
        expiredPostIds.forEach(postId -> expiredPosts.add(PostRedis.builder().id(postId).build()));

        List<Long> notExpiredPostIds = new ArrayList<>(postIdsWithoutLastPostId);
        notExpiredPostIds.removeAll(expiredPostIds);
        List<PostRedis> notExpiredPosts = new ArrayList<>();
        notExpiredPostIds.forEach(postId -> notExpiredPosts.add(PostRedis.builder().id(postId).build()));

        result = new TreeSet<>();
        result.addAll(expiredPosts);
        result.addAll(notExpiredPosts);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(postRedisService.getAllByIds(postIdsWithoutLastPostId)).thenReturn(notExpiredPosts);
        when(postService.findAllByIdsWithLikes(expiredPostIds)).thenReturn(expiredPosts);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, null);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postRedisService, times(1)).getAllByIds(postIdsWithoutLastPostId);
        verify(postService, times(1)).findAllByIdsWithLikes(expiredPostIds);
        verify(postRedisService,times(1)).setCommentsFromDB(expiredPosts);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenSomePostsInCacheExpired() {
        List<Long> expiredPostIds = new ArrayList<>();
        expiredPostIds.add(postIdsWithLastPostId.get(batchSize - 2));
        expiredPostIds.add(postIdsWithLastPostId.get(batchSize - 1));
        List<PostRedis> expiredPosts = new ArrayList<>();
        expiredPostIds.forEach(postId -> expiredPosts.add(PostRedis.builder().id(postId).build()));

        List<Long> notExpiredPostIds = new ArrayList<>(postIdsWithLastPostId);
        notExpiredPostIds.removeAll(expiredPostIds);
        List<PostRedis> notExpiredPosts = new ArrayList<>();
        notExpiredPostIds.forEach(postId -> notExpiredPosts.add(PostRedis.builder().id(postId).build()));

        result = new TreeSet<>();
        result.addAll(expiredPosts);
        result.addAll(notExpiredPosts);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(postRedisService.getAllByIds(postIdsWithLastPostId)).thenReturn(notExpiredPosts);
        when(postService.findAllByIdsWithLikes(expiredPostIds)).thenReturn(expiredPosts);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postRedisService, times(1)).getAllByIds(postIdsWithLastPostId);
        verify(postService, times(1)).findAllByIdsWithLikes(expiredPostIds);
        verify(postRedisService,times(1)).setCommentsFromDB(expiredPosts);
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(0)).findByAuthorsBeforeId(anyList(), anyLong(), anyInt());
        verify(postRedisService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithoutLastPostIdWhenPostsNotEnoughInCache() {
        result = new TreeSet<>();
        result.addAll(notEnoughPosts);
        Long lastPostIdForBD = result.last().getId();
        result.addAll(extraPosts);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(notEnoughPostIds);
        when(postRedisService.getAllByIds(notEnoughPostIds)).thenReturn(notEnoughPosts);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostIdForBD, extraPostIds.size())).thenReturn(extraPosts);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, null);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postRedisService, times(1)).getAllByIds(notEnoughPostIds);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1))
                .findByAuthorsBeforeId(followeeIds, lastPostIdForBD, extraPosts.size());
        verify(postRedisService, times(1)).setCommentsFromDB(extraPosts);
        verify(postRedisService, times(1)).setAuthors(result);
        assertEquals(result, actual);
    }

    @Test
    void testGetNewsFeedWithLastPostIdWhenPostsNotEnoughInCache() {
        lastPostId = 8L;

        result = new TreeSet<>();
        result.addAll(notEnoughPosts);
        Long lastPostIdForDB = result.last().getId();
        result.addAll(extraPosts);
        when(newsFeedRedisRepository.getSortedPostIds(key)).thenReturn(postIds);
        when(postRedisService.getAllByIds(notEnoughPostIds)).thenReturn(notEnoughPosts);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findByAuthorsBeforeId(followeeIds, lastPostIdForDB, extraPostIds.size())).thenReturn(extraPosts);

        TreeSet<PostRedis> actual = newsFeedService.getNewsFeed(userId, lastPostId);

        verify(newsFeedRedisRepository, times(1)).getSortedPostIds(key);
        verify(postRedisService, times(1)).getAllByIds(notEnoughPostIds);
        verify(postService, times(0)).findAllByIdsWithLikes(anyList());
        verify(postService, times(0)).findByAuthors(anyList(), anyInt());
        verify(postService, times(1))
                .findByAuthorsBeforeId(followeeIds, lastPostIdForDB, extraPosts.size());
        verify(postRedisService, times(1)).setCommentsFromDB(extraPosts);
        verify(postRedisService, times(1)).setAuthors(result);
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
        when(newsFeedRedisRepository.getSize(key)).thenReturn(newsFeedMaxSize - 10L);

        newsFeedService.addPost(userId, postId);

        verify(newsFeedRedisRepository, times(1)).addPostId(key, postId);
        verify(newsFeedRedisRepository, times(1)).getSize(key);
        verify(newsFeedRedisRepository, times(0)).removeLastPostId(key);
    }

    @Test
    void testAddPostWhenFeedSizeIsMax() {
        when(newsFeedRedisRepository.getSize(key))
                .thenReturn((long) newsFeedMaxSize + 1)
                .thenReturn((long) newsFeedMaxSize);

        newsFeedService.addPost(userId, postId);

        verify(newsFeedRedisRepository, times(1)).addPostId(key, postId);
        verify(newsFeedRedisRepository, times(2)).getSize(key);
        verify(newsFeedRedisRepository, times(1)).removeLastPostId(key);
    }

    @Test
    void testAddPostWhenFeedSizeIsMoreMaxSize() {
        when(newsFeedRedisRepository.getSize(key))
                .thenReturn((long) newsFeedMaxSize + 4)
                .thenReturn((long) newsFeedMaxSize + 3)
                .thenReturn((long) newsFeedMaxSize + 2)
                .thenReturn((long) newsFeedMaxSize + 1)
                .thenReturn((long) newsFeedMaxSize);

        newsFeedService.addPost(userId, postId);

        verify(newsFeedRedisRepository, times(1)).addPostId(key, postId);
        verify(newsFeedRedisRepository, times(5)).getSize(key);
        verify(newsFeedRedisRepository, times(4)).removeLastPostId(key);
    }

    @Test
    void testSaveAllNewsFeeds() {
        NewsFeedRedis firstFeed = new NewsFeedRedis(2L, List.of(1L, 2L, 3L));
        NewsFeedRedis secondFeed = new NewsFeedRedis(2L, List.of(4L, 5L, 6L));
        NewsFeedRedis thirdFeed = new NewsFeedRedis(2L, List.of(7L, 8L, 9L));
        List<NewsFeedRedis> newsFeeds = List.of(firstFeed, secondFeed, thirdFeed);

        newsFeedService.saveAllNewsFeeds(newsFeeds);

        verify(newsFeedRedisRepository, times(1))
                .addAll(newsFeedPrefix + firstFeed.getFollowerId(), firstFeed.getPostIds());
        verify(newsFeedRedisRepository, times(1))
                .addAll(newsFeedPrefix + secondFeed.getFollowerId(), secondFeed.getPostIds());
        verify(newsFeedRedisRepository, times(1))
                .addAll(newsFeedPrefix + thirdFeed.getFollowerId(), thirdFeed.getPostIds());
    }

    @Test
    void testGetNewsFeedsForUsers() {
        UserRedis firstUser = new UserRedis(1L, "username");
        UserRedis secondUser = new UserRedis(2L, "username");
        UserRedis thirdUser = new UserRedis(3L, "username");
        List<UserRedis> usersRedis = List.of(firstUser, secondUser, thirdUser);

        List<Long> firstUserPostIds = List.of(1L, 2L, 3L);
        List<Long> secondUserPostIds = List.of(4L, 5L, 6L);
        List<Long> thirdUserPostIds = new ArrayList<>();

        List<NewsFeedRedis> expected = List.of(
                new NewsFeedRedis(firstUser.getId(), firstUserPostIds),
                new NewsFeedRedis(secondUser.getId(), secondUserPostIds)
        );

        when(postService.findPostIdsByFollowerId(firstUser.getId(), newsFeedMaxSize)).thenReturn(firstUserPostIds);
        when(postService.findPostIdsByFollowerId(secondUser.getId(), newsFeedMaxSize)).thenReturn(secondUserPostIds);
        when(postService.findPostIdsByFollowerId(thirdUser.getId(), newsFeedMaxSize)).thenReturn(thirdUserPostIds);

        List<NewsFeedRedis> actual = newsFeedService.getNewsFeedsForUsers(usersRedis);

        verify(postService, times(1)).findPostIdsByFollowerId(firstUser.getId(), newsFeedMaxSize);
        verify(postService, times(1)).findPostIdsByFollowerId(secondUser.getId(), newsFeedMaxSize);
        verify(postService, times(1)).findPostIdsByFollowerId(thirdUser.getId(), newsFeedMaxSize);
        assertEquals(expected, actual);
    }
}