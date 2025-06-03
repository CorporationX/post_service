package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CacheOperationException;
import faang.school.postservice.exception.PostRetrievalException;
import faang.school.postservice.exception.ServiceUnavailableException;
import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedCacheServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostCacheMapper postCacheMapper;

    @InjectMocks
    private FeedCacheServiceImpl feedCacheService;

    private static final Long USER_ID = 1L;
    private static final Set<Long> SUBSCRIPTIONS = Set.of(2L, 3L);
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "publishedAt"));

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedCacheService, "postsLimit", 10);
        ReflectionTestUtils.setField(feedCacheService, "userFeedKeyPattern", "feed:%s");
        ReflectionTestUtils.setField(feedCacheService, "userDataKeyPattern", "user:%s");
        ReflectionTestUtils.setField(feedCacheService, "postDataKeyPattern", "post:%s");
        ReflectionTestUtils.setField(feedCacheService, "defaultTtlSeconds", 86400L);
    }

    @Test
    void testWarmUpCacheForUser_WithSubscriptionsAndPosts() {
        Post post1 = createPost(100L, 2L, "Content 1");
        Post post2 = createPost(101L, 3L, "Content 2");
        List<Post> posts = List.of(post1, post2);

        Set<Long> authorIds = Set.of(2L, 3L);
        Set<Long> commentAuthorIds = Set.of(4L);
        Set<Long> allAuthors = new HashSet<>();
        allAuthors.addAll(authorIds);
        allAuthors.addAll(commentAuthorIds);

        when(userServiceClient.getSubscriptions(USER_ID)).thenReturn(SUBSCRIPTIONS);
        when(postRepository.findByAuthorIdsAndPublishedTrue(eq(SUBSCRIPTIONS), eq(PAGE_REQUEST))).thenReturn(posts);
        when(postRepository.findAllByIdWithComments(anySet())).thenReturn(posts);
        when(postCacheMapper.postToCacheDto(any(Post.class))).thenReturn(new PostCacheDto());

        List<UserDto> users = allAuthors.stream()
                .map(id -> new UserDto(id, "User " + id, "user" + id + "@example.com", Locale.ENGLISH))
                .collect(Collectors.toList());
        when(userServiceClient.getUsersByIds(new ArrayList<>(allAuthors))).thenReturn(users);

        feedCacheService.warmUpCacheForUser(USER_ID);

        verify(redisTemplate, times(3)).executePipelined(any(RedisCallback.class));
        verify(userServiceClient).getSubscriptions(USER_ID);
        verify(userServiceClient).getUsersByIds(new ArrayList<>(allAuthors));
        verify(postRepository).findByAuthorIdsAndPublishedTrue(SUBSCRIPTIONS, PAGE_REQUEST);
        verify(postRepository).findAllByIdWithComments(anySet());
    }

    @Test
    void testWarmUpCacheForUser_NoSubscriptions() {
        when(userServiceClient.getSubscriptions(USER_ID)).thenReturn(Collections.emptySet());

        feedCacheService.warmUpCacheForUser(USER_ID);

        verifyNoInteractions(postRepository);
        verifyNoInteractions(redisTemplate);
        verify(userServiceClient, never()).getUsersByIds(any());
    }

    @Test
    void testWarmUpCacheForUser_WithSubscriptionsButNoPosts() {
        when(userServiceClient.getSubscriptions(USER_ID)).thenReturn(SUBSCRIPTIONS);
        when(postRepository.findByAuthorIdsAndPublishedTrue(eq(SUBSCRIPTIONS), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        feedCacheService.warmUpCacheForUser(USER_ID);

        verify(redisTemplate, never()).executePipelined(any(RedisCallback.class));
        verify(userServiceClient, never()).getUsersByIds(any());
        verify(postRepository, never()).findAllByIdWithComments(anySet());
    }

    @Test
    void testWarmUpCacheForUser_WhenFeignException() {
        when(userServiceClient.getSubscriptions(USER_ID)).thenThrow(FeignException.class);

        assertThrows(ServiceUnavailableException.class, () -> feedCacheService.warmUpCacheForUser(USER_ID));
    }

    @Test
    void testWarmUpCacheForUser_RedisException() {
        when(userServiceClient.getSubscriptions(USER_ID)).thenReturn(SUBSCRIPTIONS);
        when(postRepository.findByAuthorIdsAndPublishedTrue(eq(SUBSCRIPTIONS), any()))
                .thenReturn(List.of(createPost(100L, 2L, "Content")));
        when(redisTemplate.executePipelined(any(RedisCallback.class)))
                .thenThrow(new RedisConnectionFailureException("Redis down"));

        assertThrows(CacheOperationException.class, () -> feedCacheService.warmUpCacheForUser(USER_ID));
    }

    @Test
    void testWarmUpCacheForUser_DatabaseException() {
        when(userServiceClient.getSubscriptions(USER_ID)).thenReturn(SUBSCRIPTIONS);
        when(postRepository.findByAuthorIdsAndPublishedTrue(eq(SUBSCRIPTIONS), any()))
                .thenThrow(new DataAccessResourceFailureException("DB error"));

        assertThrows(PostRetrievalException.class, () -> feedCacheService.warmUpCacheForUser(USER_ID));
    }

    private Post createPost(Long id, Long authorId, String content) {
        return Post.builder()
                .id(id)
                .authorId(authorId)
                .content(content)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .comments(List.of(
                        Comment.builder()
                                .id(1L)
                                .authorId(4L)
                                .content("Test comment")
                                .build()
                ))
                .build();
    }
}
