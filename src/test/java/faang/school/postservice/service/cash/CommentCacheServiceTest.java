package faang.school.postservice.service.cash;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentCacheServiceTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentCacheService commentCacheService;

    private Post testPost;
    private Comment testComment1;
    private Comment testComment2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commentCacheService, "latestCommentsCount", 3);

        now = LocalDateTime.now();
        testPost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Test post content")
                .build();

        testComment1 = Comment.builder()
                .id(1L)
                .authorId(2L)
                .content("Test comment 1")
                .post(testPost)
                .createdAt(now)
                .updatedAt(now)
                .build();

        testComment2 = Comment.builder()
                .id(2L)
                .authorId(3L)
                .content("Test comment 2")
                .post(testPost)
                .createdAt(now.plusMinutes(1))
                .updatedAt(now.plusMinutes(1))
                .build();
    }

    @Test
    @DisplayName("Fetch latest comments when comments exist should return cached comments")
    void fetchLatestComments_WhenCommentsExist_ShouldReturnCachedComments() {
        List<Comment> comments = Arrays.asList(testComment1, testComment2);
        when(commentService.getLatestCommentsByPostId(testPost.getId(), 3))
                .thenReturn(comments);
        LinkedHashSet<CommentCache> result = commentCacheService.fetchLatestComments(testPost.getId());
        assertNotNull(result);
        assertEquals(2, result.size());
        CommentCache[] cachedComments = result.toArray(new CommentCache[0]);
        assertEquals(testComment1.getId(), cachedComments[0].getId());
        assertEquals(testComment1.getAuthorId(), cachedComments[0].getAuthorId());
        assertEquals(testComment1.getContent(), cachedComments[0].getContent());
        assertEquals(testComment1.getPost().getId(), cachedComments[0].getPostId());
        assertEquals(testComment1.getCreatedAt(), cachedComments[0].getCreatedAt());
        assertEquals(testComment1.getUpdatedAt(), cachedComments[0].getUpdatedAt());
        assertEquals(testComment2.getId(), cachedComments[1].getId());
        assertEquals(testComment2.getAuthorId(), cachedComments[1].getAuthorId());
        assertEquals(testComment2.getContent(), cachedComments[1].getContent());
        assertEquals(testComment2.getPost().getId(), cachedComments[1].getPostId());
        assertEquals(testComment2.getCreatedAt(), cachedComments[1].getCreatedAt());
        assertEquals(testComment2.getUpdatedAt(), cachedComments[1].getUpdatedAt());
    }

    @Test
    @DisplayName("Fetch latest comments when no comments exist should return empty set")
    void fetchLatestComments_WhenNoComments_ShouldReturnEmptySet() {
        when(commentService.getLatestCommentsByPostId(testPost.getId(), 3))
                .thenReturn(List.of());
        LinkedHashSet<CommentCache> result = commentCacheService.fetchLatestComments(testPost.getId());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Fetch latest comments when service throws exception should return empty set")
    void fetchLatestComments_WhenServiceThrowsException_ShouldReturnEmptySet() {
          when(commentService.getLatestCommentsByPostId(testPost.getId(), 3))
                .thenThrow(new RuntimeException("Service error"));
        LinkedHashSet<CommentCache> result = commentCacheService.fetchLatestComments(testPost.getId());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Fetch latest comments with null fields should handle gracefully")
    void fetchLatestComments_WhenCommentHasNullFields_ShouldHandleGracefully() {
        Comment nullFieldsComment = Comment.builder()
                .id(3L)
                .post(testPost)
                .build();
        when(commentService.getLatestCommentsByPostId(testPost.getId(), 3))
                .thenReturn(List.of(nullFieldsComment));
        LinkedHashSet<CommentCache> result = commentCacheService.fetchLatestComments(testPost.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        CommentCache cachedComment = result.iterator().next();
        assertEquals(3L, cachedComment.getId());
        assertNull(cachedComment.getAuthorId());
        assertNull(cachedComment.getContent());
        assertEquals(testPost.getId(), cachedComment.getPostId());
        assertNull(cachedComment.getCreatedAt());
        assertNull(cachedComment.getUpdatedAt());
    }

    @Test
    @DisplayName("Fetch latest comments should respect configured limit")
    void fetchLatestComments_ShouldRespectConfiguredLimit() {
        ReflectionTestUtils.setField(commentCacheService, "latestCommentsCount", 1);
        List<Comment> comments = Arrays.asList(testComment1, testComment2);
        when(commentService.getLatestCommentsByPostId(testPost.getId(), 1))
                .thenReturn(List.of(testComment1));
        LinkedHashSet<CommentCache> result = commentCacheService.fetchLatestComments(testPost.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentService).getLatestCommentsByPostId(testPost.getId(), 1);
    }
}