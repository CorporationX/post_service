package faang.school.postservice.service.feed.warmup;

import faang.school.postservice.cache.author.AuthorCache;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.user.feed.HeatUserTask;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.follow.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedWarmupServiceTest {

    private static final long USER_ID = 7L;

    @Mock
    private FollowService followService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FeedCache feedCache;

    @Mock
    private PostCache postCache;

    @Mock
    private AuthorCache authorCache;

    private final FeedMapper feedMapper = Mappers.getMapper(FeedMapper.class);

    private FeedWarmupServiceImpl service;

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    @Captor
    private ArgumentCaptor<List<Instant>> instantsCaptor;

    @Captor
    private
    ArgumentCaptor<List<PostCacheDto>> postCacheDtosCaptor;

    @Captor
    private ArgumentCaptor<List<Long>> authorIdsCaptor;

    @BeforeEach
    public void setUp() {
        service = new FeedWarmupServiceImpl(
                followService,
                postRepository,
                feedCache,
                postCache,
                authorCache,
                feedMapper
        );
    }

    @Test
    @DisplayName("No following → does nothing")
    public void warmUpUserNoFollowing() {
        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of());

        service.warmUpUser(new HeatUserTask(USER_ID, 5));

        verifyNoInteractions(postRepository, feedCache, postCache, authorCache);
    }

    @Test
    @DisplayName("No recent posts → does nothing")
    public void warmUpUserNoRecentPosts() {
        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of(10L, 20L));
        when(postRepository.findRecentByAuthors(List.of(10L, 20L), 5)).thenReturn(List.of());

        service.warmUpUser(new HeatUserTask(USER_ID, 5));

        verify(postRepository).findRecentByAuthors(List.of(10L, 20L), 5);
        verifyNoInteractions(feedCache, postCache, authorCache);
    }

    @Test
    @DisplayName("Adds posts to feed, caches posts, preloads unique authors")
    public void warmUpUserHappyPath() {
        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of(10L, 20L));

        Post p1 = post(101L, 10L);
        Post p2 = post(102L, 10L);
        Post p3 = post(103L, 30L);
        when(postRepository.findRecentByAuthors(List.of(10L, 20L), 5))
                .thenReturn(List.of(p1, p2, p3));

        doNothing().when(feedCache).addAllPostsForUser(eq(USER_ID), anyList(), anyList());
        doNothing().when(postCache).putAll(anyList());
        doNothing().when(authorCache).preloadAll(anyList());

        service.warmUpUser(new HeatUserTask(USER_ID, 5));

        verify(feedCache).addAllPostsForUser(eq(USER_ID), idsCaptor.capture(), instantsCaptor.capture());
        List<Long> ids = idsCaptor.getValue();
        List<Instant> instants = instantsCaptor.getValue();
        assertThat(ids).containsExactly(101L, 102L, 103L);
        assertThat(instants).hasSize(3);

        verify(postCache).putAll(postCacheDtosCaptor.capture());
        assertThat(postCacheDtosCaptor.getValue()).hasSize(3);

        verify(authorCache).preloadAll(authorIdsCaptor.capture());
        assertThat(authorIdsCaptor.getValue())
                .containsExactlyInAnyOrder(10L, 30L);
    }

    @Test
    @DisplayName("Skips null authorId when preloading authors")
    public void warmUpUserSkipsNullAuthorId() {
        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of(10L));
        Post p1 = post(201L, null);
        Post p2 = post(202L, 40L);
        when(postRepository.findRecentByAuthors(List.of(10L), 3)).thenReturn(List.of(p1, p2));

        doNothing().when(feedCache).addAllPostsForUser(eq(USER_ID), anyList(), anyList());
        doNothing().when(postCache).putAll(anyList());
        doNothing().when(authorCache).preloadAll(anyList());

        service.warmUpUser(new HeatUserTask(USER_ID, 3));

        verify(authorCache).preloadAll(authorIdsCaptor.capture());
        assertThat(authorIdsCaptor.getValue()).containsExactly(40L);
    }

    private static Post post(Long id, Long authorId) {
        Post p = new Post();
        p.setId(id);
        p.setAuthorId(authorId);
        return p;
    }
}