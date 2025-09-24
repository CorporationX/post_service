package faang.school.postservice.service.feed;

import faang.school.postservice.cache.author.AuthorCache;
import faang.school.postservice.cache.comment.PostCommentCache;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.config.properties.cache.comment.CommentCacheProperties;
import faang.school.postservice.config.properties.cache.feed.FeedProperties;
import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.follow.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    private static final long USER_ID = 10L;

    @Mock
    private FeedCache feedCache;

    @Mock
    private PostCache postCache;

    @Mock
    private AuthorCache authorCache;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FollowService followService;

    @Mock
    private PostCommentCache postCommentCache;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private FeedMapper feedMapper = Mappers.getMapper(FeedMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @BeforeEach
    void setUp() {
        FeedProperties feedProperties = new FeedProperties(2);
        CommentCacheProperties commentProps = new CommentCacheProperties(
                "feed:comment:list:",
                "feed:comment:ids:",
                "feed:comment:set:",
                3
        );

        service = new FeedServiceImpl(commentProps, feedProperties, feedCache, postCache, authorCache, postRepository,
                followService, feedMapper, postCommentCache, commentRepository, commentMapper
        );
    }

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    @Captor
    private ArgumentCaptor<List<Instant>> scoresCaptor;

    @InjectMocks
    private FeedServiceImpl service;

    @Test
    @DisplayName("Returns page from cache")
    public void getFeedPageFromCacheOnly() {
        when(feedCache.getIds(USER_ID, null, 2)).thenReturn(List.of(101L, 102L));

        PostCacheDto pc1 = new PostCacheDto(101L, "t1", 1001L, null, null, null, null);
        PostCacheDto pc2 = new PostCacheDto(102L, "t2", 1002L, null, null, null, null);
        when(postCache.getAll(List.of(101L, 102L))).thenReturn(List.of(pc1, pc2));

        AuthorCacheDto a1 = new AuthorCacheDto(1001L, "A1", "a1.png");
        AuthorCacheDto a2 = new AuthorCacheDto(1002L, "A2", "a2.png");
        when(authorCache.getAll(List.of(1001L, 1002L))).thenReturn(List.of(a1, a2));

        List<FeedCommentCacheDto> c101 = List.of(new FeedCommentCacheDto(1L, 200L, "cmt1", Instant.now()));
        List<FeedCommentCacheDto> c102 = List.of(new FeedCommentCacheDto(2L, 201L, "cmt2", Instant.now()));
        when(postCommentCache.getLast(101L, 3)).thenReturn(c101);
        when(postCommentCache.getLast(102L, 3)).thenReturn(c102);

        List<FeedPostDto> page = service.getFeedPage(USER_ID, null);

        assertThat(page).hasSize(2);
        assertThat(page.get(0).id()).isEqualTo(101L);
        assertThat(page.get(1).id()).isEqualTo(102L);
        assertThat(page.get(0).lastComments()).isNotNull();
        assertThat(page.get(1).lastComments()).isNotNull();

        verify(feedCache).getIds(USER_ID, null, 2);
        verify(postCache).getAll(List.of(101L, 102L));
        verify(authorCache).getAll(List.of(1001L, 1002L));
        verify(postCommentCache).getLast(101L, 3);
        verify(postCommentCache).getLast(102L, 3);
        verifyNoInteractions(postRepository, commentRepository, followService);
    }

    @Test
    @DisplayName("Falls back to DB when feed cache empty")
    public void getFeedPageFromDbWhenFeedEmpty() {
        when(feedCache.getIds(USER_ID, null, 2)).thenReturn(List.of());
        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of(11L, 22L));

        Post p1 = post(201L, 11L, nowMinusMinutes(10));
        Post p2 = post(202L, 22L, nowMinusMinutes(5));
        when(postRepository.findRecentByAuthors(List.of(11L, 22L), 2)).thenReturn(List.of(p1, p2));

        AuthorCacheDto a1 = new AuthorCacheDto(11L, "U11", "u11.png");
        AuthorCacheDto a2 = new AuthorCacheDto(22L, "U22", "u22.png");
        when(authorCache.getAll(List.of(11L, 22L))).thenReturn(List.of(a1, a2));

        when(postCommentCache.getLast(201L, 3)).thenReturn(List.of());
        when(postCommentCache.getLast(202L, 3)).thenReturn(List.of());
        when(commentRepository.findLatestByPostId(201L, 3))
                .thenReturn(List.of(comment(901L, 201L, "db1")));
        when(commentRepository.findLatestByPostId(202L, 3))
                .thenReturn(List.of(comment(902L, 202L, "db2")));

        doNothing().when(postCache).putAll(anyList());

        List<FeedPostDto> page = service.getFeedPage(USER_ID, null);

        assertThat(page).hasSize(2);
        assertThat(page.get(0).id()).isEqualTo(201L);
        assertThat(page.get(1).id()).isEqualTo(202L);

        verify(feedCache).addAllPostsForUser(eq(USER_ID), idsCaptor.capture(), scoresCaptor.capture());
        assertThat(idsCaptor.getValue()).containsExactly(201L, 202L);
        assertThat(scoresCaptor.getValue()).hasSize(2);

        verify(followService).getAllFollowingAuthorIds(USER_ID);
        verify(postRepository).findRecentByAuthors(List.of(11L, 22L), 2);
        verify(postCache).putAll(anyList());
        verify(authorCache).getAll(List.of(11L, 22L));
        verify(commentRepository).findLatestByPostId(201L, 3);
        verify(commentRepository).findLatestByPostId(202L, 3);
    }

    @Test
    @DisplayName("Loads remainder from DB")
    public void getFeedPageLoadsRemainingFromDb() {
        when(feedCache.getIds(USER_ID, null, 2)).thenReturn(List.of(300L));

        PostCacheDto cached = new PostCacheDto(300L, "t", 77L, null, null, null, null);
        when(postCache.getAll(List.of(300L))).thenReturn(List.of(cached));
        when(authorCache.getAll(List.of(77L)))
                .thenReturn(List.of(new AuthorCacheDto(77L, "AU", "au.png")));

        when(followService.getAllFollowingAuthorIds(USER_ID)).thenReturn(List.of(77L, 88L));

        Post pNext = post(301L, 88L, nowMinusMinutes(1));
        when(postRepository.findRecentByAuthorsAfterId(List.of(77L, 88L), 300L, 1))
                .thenReturn(List.of(pNext));
        when(authorCache.getAll(List.of(88L)))
                .thenReturn(List.of(new AuthorCacheDto(88L, "B", "b.png")));

        when(postCommentCache.getLast(300L, 3)).thenReturn(List.of());
        when(postCommentCache.getLast(301L, 3)).thenReturn(List.of());
        when(commentRepository.findLatestByPostId(300L, 3)).thenReturn(List.of());
        when(commentRepository.findLatestByPostId(301L, 3)).thenReturn(List.of());

        doNothing().when(postCache).putAll(anyList());

        List<FeedPostDto> page = service.getFeedPage(USER_ID, null);

        assertThat(page).hasSize(2);
        assertThat(page.get(0).id()).isEqualTo(300L);
        assertThat(page.get(1).id()).isEqualTo(301L);

        verify(postRepository).findRecentByAuthorsAfterId(List.of(77L, 88L), 300L, 1);
        verify(feedCache).addAllPostsForUser(eq(USER_ID), anyList(), anyList());
    }

    @Test
    @DisplayName("Comment cache preferred")
    public void getFeedPageCommentsPreferCache() {
        when(feedCache.getIds(USER_ID, null, 2)).thenReturn(List.of(400L));

        PostCacheDto pc = new PostCacheDto(400L, "t", 55L, null, null, null, null);
        when(postCache.getAll(List.of(400L))).thenReturn(List.of(pc));
        when(authorCache.getAll(List.of(55L))).thenReturn(List.of(new AuthorCacheDto(55L, "N", "n.png")));

        List<FeedCommentCacheDto> cached = List.of(new FeedCommentCacheDto(1L, 2L, "in-cache", Instant.now()));
        when(postCommentCache.getLast(400L, 3)).thenReturn(cached);

        List<FeedPostDto> page = service.getFeedPage(USER_ID, null);

        assertThat(page).hasSize(1);
        List<FeedCommentDto> last = page.get(0).lastComments();
        assertThat(last).isNotNull();
        assertThat(last).hasSize(1);

        verifyNoInteractions(commentRepository);
    }


    private static Post post(long id, long authorId, LocalDateTime publishedAt) {
        Post p = new Post();
        p.setId(id);
        p.setAuthorId(authorId);
        p.setPublishedAt(publishedAt);
        return p;
    }

    private static Comment comment(long id, long postId, String text) {
        Comment c = new Comment();
        c.setId(id);
        c.setContent(text);
        c.setAuthorId(123L);
        Post p = new Post();
        p.setId(postId);
        c.setPost(p);
        c.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
        return c;
    }

    private static LocalDateTime nowMinusMinutes(int minutes) {
        return LocalDateTime.now(Clock.systemUTC()).minusMinutes(minutes);
    }
}
