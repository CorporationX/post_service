package faang.school.postservice.service.feed;

import faang.school.postservice.cache.user.FeedCache;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsFeedServiceTest {
    private static final int PER_PAGE = 10;
    private static final long USER_ID = 1;
    private static final long SEARCH_AFTER = 1;

    @InjectMocks
    private NewsFeedServiceImpl service;

    @Mock
    private FeedCache feedCache;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "perPage", PER_PAGE);

        when(userContext.getUserId()).thenReturn(USER_ID);
    }

    @Test
    void whenCacheHasFullPageThenReturnCacheAndDoNotCallDb() {
        List<PostDto> cached = generatePostDtoLists(PER_PAGE);
        FeedRequest request = new FeedRequest(SEARCH_AFTER);

        when(feedCache.getUserFeed(USER_ID, request, PER_PAGE)).thenReturn(cached);

        List<PostDto> result = service.getUserFeed(request);

        assertEquals(PER_PAGE, result.size());
        assertSame(cached, result);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(postMapper);
    }

    @Test
    void whenCachePartialThenUseCacheLastIdAsCursorAndFetchRemainingFromDb() {
        FeedRequest request = new FeedRequest(SEARCH_AFTER);
        List<PostDto> cached = generatePostDtoLists(PER_PAGE - 1);
        int remaining = PER_PAGE - cached.size();
        List<Post> dbPosts = generatePostLists(remaining);
        int lastCachedPostId = cached.size() - 1;
        when(feedCache.getUserFeed(USER_ID, request, PER_PAGE)).thenReturn(cached);
        when(postRepository.getPostsAfterIdForFollower(lastCachedPostId, USER_ID, remaining)).thenReturn(dbPosts);


        List<PostDto> result = service.getUserFeed(request);

        assertEquals(PER_PAGE, result.size());
        verify(postRepository, times(1)).getPostsAfterIdForFollower(lastCachedPostId, USER_ID, remaining);
        verify(postMapper, times(1)).toPostDtoList(dbPosts);
    }

    @Test
    void whenCacheEmptyAndRequestHasSearchAfterThenUseSearchAfterAsCursor() {
        FeedRequest request = new FeedRequest(SEARCH_AFTER);
        when(feedCache.getUserFeed(USER_ID, request, PER_PAGE)).thenReturn(new ArrayList<>());

        List<Post> dbPosts = generatePostLists(PER_PAGE);
        when(postRepository.getPostsAfterIdForFollower(SEARCH_AFTER, USER_ID, PER_PAGE)).thenReturn(dbPosts);

        List<PostDto> result = service.getUserFeed(request);

        assertEquals(PER_PAGE, result.size());

        verify(postRepository, times(1)).getPostsAfterIdForFollower(SEARCH_AFTER, USER_ID, PER_PAGE);
        verify(postMapper, times(1)).toPostDtoList(dbPosts);
    }

    @Test
    void whenCacheEmptyAndNoSearchAfterThenGetLatestFromDb() {
        FeedRequest request = new FeedRequest(null);
        when(feedCache.getUserFeed(USER_ID, request, PER_PAGE)).thenReturn(new ArrayList<>());

        List<Post> dbPosts = generatePostLists(PER_PAGE);
        when(postRepository.getPostsForFollower(USER_ID, PER_PAGE)).thenReturn(dbPosts);

        List<PostDto> result = service.getUserFeed(request);

        assertEquals(PER_PAGE, result.size());

        verify(postRepository, times(1)).getPostsForFollower(USER_ID, PER_PAGE);
        verify(postMapper, times(1)).toPostDtoList(dbPosts);
    }

    List<PostDto> generatePostDtoLists(int quantity) {
        List<PostDto> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (long i = 0; i < quantity; i++) {
            result.add(
                    PostDto.builder()
                            .id(i)
                            .content(String.valueOf(i))
                            .authorId(i)
                            .projectId(i)
                            .publishedAt(now)
                            .createdAt(now)
                            .build()
            );
        }
        return result;
    }

    List<Post> generatePostLists(int quantity) {
        List<Post> result = new ArrayList<>();
        for (long i = 0; i < quantity; i++) {
            result.add(mock(Post.class));
        }
        return result;
    }
}
