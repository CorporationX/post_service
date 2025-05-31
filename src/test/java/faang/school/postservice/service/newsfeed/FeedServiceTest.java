package faang.school.postservice.service.newsfeed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisKeyConstants;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private RedisTemplate<String, Object> feedRedisTemplate;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private FeedService feedService;

    private final Long DEFAULT_USER_ID = 1L;
    private final UserDto DEFAULT_USER_DTO = new UserDto(DEFAULT_USER_ID, "testUser", "test@email.com");
    private final String FEED_KEY = RedisKeyConstants.FEED_KEY_PREFIX.getValue() + DEFAULT_USER_ID;

    @BeforeEach
    void setUp() {
        lenient().when(userContext.getUserId()).thenReturn(DEFAULT_USER_ID);
        lenient().when(feedRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void getFeedSuccessfully() {
        int page = 0;
        int size = 2;
        long start = 0L;
        long end = 1L;

        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);

        KafkaTimePostIdEvent event1 = KafkaTimePostIdEvent.builder().id(101L).publishedAt(1000L).build();
        KafkaTimePostIdEvent event2 = KafkaTimePostIdEvent.builder().id(102L).publishedAt(900L).build();

        Set<ZSetOperations.TypedTuple<Object>> redisFeedItems = new LinkedHashSet<>();
        redisFeedItems.add(ZSetOperations.TypedTuple.of(event1, 1000.0));
        redisFeedItems.add(ZSetOperations.TypedTuple.of(event2, 900.0));
        when(zSetOperations.reverseRangeWithScores(FEED_KEY, start, end)).thenReturn(redisFeedItems);

        List<Long> expectedPostIds = List.of(101L, 102L);

        Post post1 = Post.builder().id(101L).content("Post 1").publishedAt(LocalDateTime.now()).build();
        Post post2 = Post.builder().id(102L).content("Post 2").publishedAt(LocalDateTime.now().minusHours(1)).build();
        when(postRepository.findAllById(expectedPostIds)).thenReturn(List.of(post1, post2));

        PostDto dto1 = new PostDto(); dto1.setId(101L);
        PostDto dto2 = new PostDto(); dto2.setId(102L);
        when(postMapper.toDto(post1)).thenReturn(dto1);
        when(postMapper.toDto(post2)).thenReturn(dto2);

        List<PostDto> result = feedService.getFeed(page, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());

        verify(userServiceClient).getUser(DEFAULT_USER_ID);
        verify(zSetOperations).reverseRangeWithScores(FEED_KEY, start, end);
        verify(postRepository).findAllById(expectedPostIds);
        verify(postMapper).toDto(post1);
        verify(postMapper).toDto(post2);
    }

    @Test
    void getFeedUserNotFound() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(null);

        List<PostDto> result = feedService.getFeed(0, 10);

        assertTrue(result.isEmpty());
        verify(userServiceClient).getUser(DEFAULT_USER_ID);
        verifyNoInteractions(zSetOperations, postRepository, postMapper);
    }

    @Test
    void getFeedUserServiceClient() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenThrow(new RuntimeException("Service unavailable"));

        List<PostDto> result = feedService.getFeed(0, 10);

        assertTrue(result.isEmpty());
        verify(userServiceClient).getUser(DEFAULT_USER_ID);
        verifyNoInteractions(zSetOperations, postRepository, postMapper);
    }

    @Test
    void getFeedInCacheIsEmpty() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);
        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 9L)).thenReturn(Collections.emptySet());

        List<PostDto> result = feedService.getFeed(0, 10);

        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRangeWithScores(FEED_KEY, 0L, 9L);
        verifyNoInteractions(postRepository, postMapper);
    }

    @Test
    void getFeedInCacheIsNull() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);
        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 9L)).thenReturn(null);

        List<PostDto> result = feedService.getFeed(0, 10);

        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRangeWithScores(FEED_KEY, 0L, 9L);
        verifyNoInteractions(postRepository, postMapper);
    }

    @Test
    void getFeedPostsNotFoundInRepository() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);

        KafkaTimePostIdEvent event1 = KafkaTimePostIdEvent.builder().id(101L).build();
        Set<ZSetOperations.TypedTuple<Object>> redisFeedItems = Set.of(ZSetOperations.TypedTuple.of(event1, 1000.0));
        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 9L)).thenReturn(redisFeedItems);

        List<Long> postIdsFromCache = List.of(101L);
        when(postRepository.findAllById(postIdsFromCache)).thenReturn(Collections.emptyList());

        List<PostDto> result = feedService.getFeed(0, 10);

        assertTrue(result.isEmpty());
        verify(postRepository).findAllById(postIdsFromCache);
        verifyNoInteractions(postMapper);
    }

    @Test
    void getFeedUnexpectedObjectTypeInZSet() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);

        KafkaTimePostIdEvent validEvent = KafkaTimePostIdEvent.builder().id(101L).build();
        String invalidObject = "I am not a post ID";

        Set<ZSetOperations.TypedTuple<Object>> redisFeedItems = new LinkedHashSet<>();
        redisFeedItems.add(ZSetOperations.TypedTuple.of(validEvent, 1000.0));
        redisFeedItems.add(ZSetOperations.TypedTuple.of(invalidObject, 900.0));

        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 9L)).thenReturn(redisFeedItems);

        Post post1 = Post.builder().id(101L).build();
        when(postRepository.findAllById(List.of(101L))).thenReturn(List.of(post1));
        PostDto dto1 = new PostDto(); dto1.setId(101L);
        when(postMapper.toDto(post1)).thenReturn(dto1);

        List<PostDto> result = feedService.getFeed(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getId());
    }

    @Test
    void getFeedNumericObjectTypeInZSet() {
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);

        Long numericPostId = 102L;

        Set<ZSetOperations.TypedTuple<Object>> redisFeedItems = new LinkedHashSet<>();
        redisFeedItems.add(ZSetOperations.TypedTuple.of(numericPostId, 1000.0));

        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 9L)).thenReturn(redisFeedItems);

        Post post1 = Post.builder().id(102L).build();
        when(postRepository.findAllById(List.of(102L))).thenReturn(List.of(post1));
        PostDto dto1 = new PostDto(); dto1.setId(102L);
        when(postMapper.toDto(post1)).thenReturn(dto1);

        List<PostDto> result = feedService.getFeed(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(102L, result.get(0).getId());
    }
    @Test
    void getFeedPreservesOrderFromRedisWhenMappingPosts() {
        int page = 0;
        int size = 3;
        when(userServiceClient.getUser(DEFAULT_USER_ID)).thenReturn(DEFAULT_USER_DTO);

        KafkaTimePostIdEvent event1 = KafkaTimePostIdEvent.builder().id(3L).build();
        KafkaTimePostIdEvent event2 = KafkaTimePostIdEvent.builder().id(1L).build();
        KafkaTimePostIdEvent event3 = KafkaTimePostIdEvent.builder().id(2L).build();

        Set<ZSetOperations.TypedTuple<Object>> redisFeedItems = new LinkedHashSet<>();
        redisFeedItems.add(ZSetOperations.TypedTuple.of(event1, 3000.0));
        redisFeedItems.add(ZSetOperations.TypedTuple.of(event2, 2000.0));
        redisFeedItems.add(ZSetOperations.TypedTuple.of(event3, 1000.0));
        when(zSetOperations.reverseRangeWithScores(FEED_KEY, 0L, 2L)).thenReturn(redisFeedItems);

        List<Long> orderedPostIdsFromCache = List.of(3L, 1L, 2L);

        Post post1_db = Post.builder().id(1L).build();
        Post post2_db = Post.builder().id(2L).build();
        Post post3_db = Post.builder().id(3L).build();

        when(postRepository.findAllById(orderedPostIdsFromCache)).thenReturn(List.of(post1_db, post2_db, post3_db));

        PostDto dto1 = new PostDto(); dto1.setId(1L);
        PostDto dto2 = new PostDto(); dto2.setId(2L);
        PostDto dto3 = new PostDto(); dto3.setId(3L);
        when(postMapper.toDto(post1_db)).thenReturn(dto1);
        when(postMapper.toDto(post2_db)).thenReturn(dto2);
        when(postMapper.toDto(post3_db)).thenReturn(dto3);

        List<PostDto> result = feedService.getFeed(page, size);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        assertEquals(2L, result.get(2).getId());
    }
}
