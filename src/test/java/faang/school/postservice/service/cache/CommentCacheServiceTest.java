package faang.school.postservice.service.cache;

import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.cache.SortedSetCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCacheServiceTest {

    @Mock
    private SortedSetCacheRepository<CommentDto> sortedSetCacheRepository;

    @Spy
    private NewsFeedProperties newsFeedProperties;

    @InjectMocks
    private CommentCacheService commentCacheService;

    private Long postId;
    private CommentDto comment;
    private List<CommentDto> comments;
    private String postIdKey;

    @BeforeEach
    void setUp() {
        postId = 1L;
        comment = new CommentDto();
        comments = List.of(comment);
        postIdKey = postId + "::comments_for_news_feed";

        newsFeedProperties.setLimitCommentsOnPost(10);

        lenient().doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(sortedSetCacheRepository).executeInOptimisticLock(any(Runnable.class), eq(postIdKey));
    }

    @Test
    void save_shouldAddCommentAndPopMinIfLimitExceeded() {
        when(sortedSetCacheRepository.size(postIdKey)).thenReturn(11L);

        commentCacheService.save(postId, comment);

        verify(sortedSetCacheRepository).put(eq(postIdKey), eq(comment), anyDouble());
        verify(sortedSetCacheRepository).executeInOptimisticLock(any(Runnable.class), eq(postIdKey));
        verify(sortedSetCacheRepository).popMin(postIdKey);
    }

    @Test
    void save_shouldNotPopMinIfLimitNotExceeded() {
        when(sortedSetCacheRepository.size(postIdKey)).thenReturn(5L);

        commentCacheService.save(postId, comment);

        verify(sortedSetCacheRepository).put(eq(postIdKey), eq(comment), anyDouble());
        verify(sortedSetCacheRepository).executeInOptimisticLock(any(Runnable.class), eq(postIdKey));
        verify(sortedSetCacheRepository, never()).popMin(postIdKey);
    }

    @Test
    void saveAll_shouldCallSaveForEachComment() {
        commentCacheService.saveAll(comments);

        verify(sortedSetCacheRepository, times(comments.size())).executeInOptimisticLock(any(Runnable.class), anyString());
    }

    @Test
    void get_shouldReturnFirstCommentIfAvailable() {
        Set<CommentDto> mockComments = Set.of(comment);
        when(sortedSetCacheRepository.get(postIdKey)).thenReturn(mockComments);

        CommentDto result = commentCacheService.get(postId);

        assertEquals(comment, result);
    }

    @Test
    void get_shouldReturnNullIfNoComments() {
        when(sortedSetCacheRepository.get(postIdKey)).thenReturn(Collections.emptySet());

        CommentDto result = commentCacheService.get(postId);

        assertNull(result);
    }

    @Test
    void getAll_shouldReturnAllComments() {
        List<CommentDto> mockComments = List.of(comment);
        when(sortedSetCacheRepository.get(postIdKey)).thenReturn(Set.copyOf(mockComments));

        List<CommentDto> result = commentCacheService.getAll(postId);

        assertEquals(mockComments, result);
    }
}
