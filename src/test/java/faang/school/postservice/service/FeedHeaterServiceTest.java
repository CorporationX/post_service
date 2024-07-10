package faang.school.postservice.service;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.FeedHeaterRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.repository.UserJdbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedHeaterServiceTest {

    private static final long POST_ID = 1L;
    private static final long USER_ID = 1L;

    @Mock
    private FeedHeaterRepository feedHeaterRepository;
    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserJdbcRepository userJdbcRepository;
    @Mock
    private RedisUserRepository redisUserRepository;

    @InjectMocks
    private FeedHeaterService feedHeaterService;

    @Test
    public void startFeedHeatTest() throws NoSuchFieldException, IllegalAccessException {
        feedHeaterService.feedHeat();
        verify(postRepository).findAllWithIdAndAuthorId();
    }

    @Test
    public void createPostWhenPostFound() {
        Post post = Post.builder()
                .id(POST_ID)
                .build();
        PostRedis postRedis = PostRedis.builder()
                .id(POST_ID)
                .build();

        when(postService.findPostWithCommentsAndLikes(POST_ID)).thenReturn(post);
        when(postMapper.toRedis(post)).thenReturn(postRedis);

        feedHeaterService.createPost(POST_ID);

        verify(redisPostRepository).save(postRedis);
    }

    @Test
    public void createPostWhenPostNotFound() {
        when(postService.findPostWithCommentsAndLikes(POST_ID)).thenReturn(null);

        feedHeaterService.createPost(POST_ID);

        verify(postMapper, never()).toRedis(any(Post.class));
        verify(redisPostRepository, never()).save(any(PostRedis.class));
    }

    @Test
    public void createAuthorWHenUserFound() {
        UserRedis userRedis = new UserRedis(USER_ID, "TEST_USER");

        when(userJdbcRepository.findUserById(USER_ID)).thenReturn(userRedis);

        feedHeaterService.createAuthor(USER_ID);

        verify(redisUserRepository).save(userRedis);
    }

    @Test
    public void createAuthorWHenUserNotFound() {
        when(userJdbcRepository.findUserById(USER_ID)).thenReturn(null);

        feedHeaterService.createAuthor(USER_ID);

        verify(redisUserRepository, never()).save(any(UserRedis.class));
    }

    @Test
    public void createFeedWhenUserHasSubscriptions() {
        when(feedHeaterRepository.findSubscriberPosts(USER_ID)).thenReturn(List.of(1L, 2L, 3L));

        feedHeaterService.addFeedForUser(USER_ID);

        verify(redisFeedRepository).save(any(FeedRedis.class));
    }

    @Test
    public void createFeedWhenUserHasNotSubscriptions() {
        when(feedHeaterRepository.findSubscriberPosts(USER_ID)).thenReturn(List.of());

        feedHeaterService.addFeedForUser(USER_ID);

        verify(redisFeedRepository, never()).save(any(FeedRedis.class));
    }
}