package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CacheCommentMapper;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.CacheCommentAuthor;
import faang.school.postservice.model.CacheUser;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.RedisAuthorCommentRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RedisAuthorCommentRepository redisAuthorCommentRepository;
    @Mock
    private PostService postService;
    @Mock
    private CachePostMapper cachePostMapper;
    @Mock
    private CacheCommentMapper cacheCommentMapper;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "sizeBatchPostToFeed", 500);
    }

    @Test
    void testAddPostToFollowers() {
        NewPostEvent newPostEvent = NewPostEvent.builder()
                .id(1L)
                .subscribersIds(List.of(1L, 2L))
                .build();

        CacheUser user1 = CacheUser.builder()
                .id(1L)
                .feed(new LinkedHashSet<>(List.of(2L)))
                .build();

        CacheUser user2 = CacheUser.builder()
                .id(2L)
                .feed(new LinkedHashSet<>(List.of(3L)))
                .build();

        when(redisUserRepository.findAllById(newPostEvent.getSubscribersIds()))
                .thenReturn(List.of(user1, user2));

        feedService.addPostToFollowers(newPostEvent);

        verify(redisUserRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testGetFeed() {
        CacheUser user = CacheUser.builder()
                .id(1L)
                .feed(new LinkedHashSet<>(List.of(1L, 2L, 3L)))
                .build();

        when(userContext.getUserId()).thenReturn(1L);
        when(redisUserRepository.findById(1L)).thenReturn(Optional.of(user));

        feedService.getFeed(null);

        verify(redisPostRepository, times(1)).findAllById(anyList(), any(PageRequest.class));
    }

    @Test
    void testAddLikeToPost() {
        CachePost cachePost = CachePost.builder()
                .id(1L)
                .countLike(5L)
                .version(1)
                .build();

        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(cachePost));

        feedService.addLikeToPost(1L);

        assertEquals(6L, cachePost.getCountLike());
        verify(redisPostRepository, times(1)).save(cachePost);
    }

    @Test
    void testAddCommentToPost() {
        CommentCache commentCache = CommentCache.builder()
                .authorId(1L)
                .content("New Comment")
                .build();

        CachePost cachePost = CachePost.builder()
                .id(1L)
                .comments(new LinkedHashSet<>())
                .version(1)
                .build();

        UserDto mockUser = UserDto.builder()
                .id(1L)
                .username("TestUser")
                .build();

        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(cachePost));
        when(userServiceClient.getUser(1L)).thenReturn(mockUser);

        feedService.addCommentToPost(1L, commentCache);

        verify(redisPostRepository, times(1)).save(cachePost);
        verify(redisAuthorCommentRepository, times(1)).save(any(CacheCommentAuthor.class));
    }


    @Test
    void testAddViewToPost() {
        CachePost cachePost = CachePost.builder()
                .id(1L)
                .countView(10L)
                .version(1)
                .build();

        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(cachePost));

        feedService.addViewToPost(1L);

        assertEquals(11L, cachePost.getCountView());
        verify(redisPostRepository, times(1)).save(cachePost);
    }
}
