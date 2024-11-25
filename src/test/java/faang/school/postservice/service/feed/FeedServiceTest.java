package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.model.*;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long START_POST_ID = 100L;
    private static final String POST_CONTENT = "Content";
    private static final Long AUTHOR_ID = 1L;
    private static final String AUTHOR_NAME = "Author";
    private static final int LIKES_COUNT = 5;
    private static final int PAGE_SIZE = 20;

    @Mock
    private RedisFeedRepository feedRedisRepository;

    @Mock
    private RedisPostRepository redisPostRepository;

    @Mock
    private PostService postService;

    @Mock
    private RedisUserRepository redisUserRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private FeedService feedService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(feedService, "pageSize", PAGE_SIZE);
    }

    @Nested
    @DisplayName("Tests for loadNextPosts")
    class LoadNextPostsTests {

        @Test
        @DisplayName("Should return empty list if feed is not found")
        void whenFeedNotFoundThenReturnEmptyList() {
            when(feedRedisRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            List<FeedPostDto> result = feedService.loadNextPosts(USER_ID, START_POST_ID);

            assertTrue(result.isEmpty());
            verify(feedRedisRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should return empty list if feed has no posts")
        void whenFeedHasNoPostsThenReturnEmptyList() {
            CachedFeedDto feed = new CachedFeedDto(USER_ID, new TreeSet<>());
            when(feedRedisRepository.findByUserId(USER_ID)).thenReturn(Optional.of(feed));

            List<FeedPostDto> result = feedService.loadNextPosts(USER_ID, START_POST_ID);

            assertTrue(result.isEmpty());
            verify(feedRedisRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should return cached posts for given user and post batch")
        void whenPostsAreCachedThenReturnPostsFromCache() {
            CachedFeedDto feed = new CachedFeedDto(USER_ID, new TreeSet<>(List.of(START_POST_ID, 101L, 102L)));
            CachedPostDto cachedPost1 = new CachedPostDto(START_POST_ID, POST_CONTENT, AUTHOR_ID, LIKES_COUNT);
            CachedPostDto cachedPost2 = new CachedPostDto(101L, "Another Content", AUTHOR_ID, 10);

            FeedPostDto expectedPost1 = FeedPostDto.builder()
                    .id(START_POST_ID)
                    .content(POST_CONTENT)
                    .authorName(AUTHOR_NAME)
                    .likes(LIKES_COUNT)
                    .build();

            FeedPostDto expectedPost2 = FeedPostDto.builder()
                    .id(101L)
                    .content("Another Content")
                    .authorName(AUTHOR_NAME)
                    .likes(10)
                    .build();

            when(feedRedisRepository.findByUserId(USER_ID)).thenReturn(Optional.of(feed));
            when(redisPostRepository.findAllById(List.of(START_POST_ID, 101L, 102L)))
                    .thenReturn(List.of(cachedPost1, cachedPost2));
            when(redisUserRepository.findById(AUTHOR_ID))
                    .thenReturn(Optional.of(new CachedFeedUserDto(AUTHOR_ID, AUTHOR_NAME)));

            List<FeedPostDto> result = feedService.loadNextPosts(USER_ID, START_POST_ID);

            assertEquals(2, result.size());
            assertEquals(expectedPost1.getContent(), result.get(0).getContent());
            assertEquals(expectedPost2.getContent(), result.get(1).getContent());
            verify(feedRedisRepository).findByUserId(USER_ID);
            verify(redisPostRepository).findAllById(List.of(START_POST_ID, 101L, 102L));
            verify(redisUserRepository, times(2)).findById(AUTHOR_ID);
        }


        @Test
        @DisplayName("Should fetch missing posts from PostService if not in cache")
        void whenPostsAreNotCachedThenFetchFromPostService() {
            CachedFeedDto feed = new CachedFeedDto(USER_ID, new TreeSet<>(List.of(START_POST_ID, 101L, 102L)));
            Post post = Post.builder()
                    .id(START_POST_ID)
                    .content(POST_CONTENT)
                    .authorId(AUTHOR_ID)
                    .likes(List.of(new Like(), new Like()))
                    .build();
            FeedPostDto expectedPost = FeedPostDto.builder()
                    .id(START_POST_ID)
                    .content(POST_CONTENT)
                    .authorName(AUTHOR_NAME)
                    .likes(2)
                    .build();

            when(feedRedisRepository.findByUserId(USER_ID)).thenReturn(Optional.of(feed));
            when(redisPostRepository.findAllById(List.of(START_POST_ID, 101L, 102L))).thenReturn(List.of());
            when(postService.findAllById(List.of(START_POST_ID, 101L, 102L))).thenReturn(List.of(post));
            when(redisUserRepository.findById(AUTHOR_ID))
                    .thenReturn(Optional.of(new CachedFeedUserDto(AUTHOR_ID, AUTHOR_NAME)));

            List<FeedPostDto> result = feedService.loadNextPosts(USER_ID, START_POST_ID);

            assertEquals(1, result.size());
            assertEquals(expectedPost.getContent(), result.get(0).getContent());
            verify(feedRedisRepository).findByUserId(USER_ID);
            verify(redisPostRepository).findAllById(List.of(START_POST_ID, 101L, 102L));
            verify(postService).findAllById(List.of(START_POST_ID, 101L, 102L));
            verify(redisUserRepository).findById(AUTHOR_ID);
        }
    }
}
