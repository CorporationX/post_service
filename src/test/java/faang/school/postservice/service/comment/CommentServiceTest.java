package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.comment.CommentValidationException;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private PostService postService;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Post post;

    @BeforeEach
    public void setUp() {
        comment = new Comment();
        comment.setId(1L);
        comment.setAuthorId(2L);
        comment.setCreatedAt(LocalDateTime.now());

        post = new Post();
        post.setId(100L);
    }

    @Test
    public void testCreateComment() {
        when(userContext.getUserId()).thenReturn(2L);

        Comment input = new Comment();
        input.setCreatedAt(LocalDateTime.now());

        when(postService.getPostById(post.getId())).thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(post.getId(), input);

        assertEquals(post, result.getPost());
        assertEquals(2L, result.getAuthorId());
        verify(commentValidator).validateCommentAuthor(2L);
        verify(commentRepository).save(result);
    }

    @Test
    public void testUpdateComment_success() {
        when(userContext.getUserId()).thenReturn(comment.getAuthorId());
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.update(comment);

        assertEquals(comment, result);
        verify(commentRepository).save(comment);
    }

    @Test
    public void testUpdateComment_unauthorized() {
        when(userContext.getUserId()).thenReturn(999L);

        assertThrows(CommentValidationException.class, () -> commentService.update(comment));
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void testGetComment_found() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        Comment result = commentService.get(comment.getId());

        assertEquals(comment, result);
    }

    @Test
    public void testGetComment_notFound() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.get(comment.getId()));
        verify(commentRepository).findById(comment.getId());
    }

    @Test
    public void testGetAllByPostId_sorted() {
        Comment older = new Comment();
        older.setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment newer = new Comment();
        newer.setCreatedAt(LocalDateTime.now());

        when(commentRepository.findAllByPostId(post.getId())).thenReturn(Arrays.asList(older, newer));

        List<Comment> result = commentService.getAllByPostId(post.getId());

        assertEquals(newer, result.get(0));
        assertEquals(older, result.get(1));
    }

    @Test
    public void testDeleteComment_success() {
        when(commentRepository.existsById(comment.getId())).thenReturn(true);

        commentService.delete(comment.getId());

        verify(commentRepository).deleteById(comment.getId());
    }

    @Test
    public void testDeleteComment_notFound() {
        when(commentRepository.existsById(comment.getId())).thenReturn(false);

        assertThrows(CommentValidationException.class, () -> commentService.delete(comment.getId()));
        verify(commentRepository, never()).deleteById(any());
    }
}
