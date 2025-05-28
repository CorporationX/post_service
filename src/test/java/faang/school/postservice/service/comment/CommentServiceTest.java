package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 2L;
    private static final long AUTHOR_ID = 3L;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidation commentValidation;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Comment commentNew;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        commentNew = new Comment();
        commentNew.setId(COMMENT_ID);
    }

    @Test
    void testCreateCommentWhenCommentCreated() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(any());
        when(commentRepository.save(comment)).thenReturn(commentNew);

        Comment result = commentService.createComment(comment);

        assertEquals(COMMENT_ID, result.getId());
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(any());
        verify(commentRepository).save(comment);
    }

    @Test
    void testUpdateCommentWhenCommentUpdate() {
        comment.setContent("Comment");
        comment.setId(COMMENT_ID);
        commentNew.setContent("New Content");

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(any());
        when(commentRepository.save(commentNew)).thenReturn(commentNew);

        Comment result = commentService.updateComment(commentNew);

        assertEquals(commentNew.getId(), result.getId());
        assertEquals(commentNew.getContent(), result.getContent());
        assertEquals(comment.getId(), result.getId());
        verify(commentRepository).findById(COMMENT_ID);
        verify(postRepository).existsById(POST_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(commentRepository).save(commentNew);
    }

    @Test
    void testUpdateCommentWhenCommentNotExists() {
        commentNew.setContent("New Content");

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(any());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentNew));
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentRepository, never()).save(any());
        verify(postRepository, never()).existsById(POST_ID);
        verify(userServiceClient, never()).getUser(AUTHOR_ID);
    }

    @Test
    void testGetAllCommentWhenPostExists() {
        comments = List.of(comment);

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);


        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(comments.get(0).getId(), result.get(0).getId());
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testGetAllCommentWhenPostNoExists() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllComments(POST_ID));
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository, never()).findAllByPostId(POST_ID);
    }

    @Test
    void testGetAllCommentWhenListCommentsEmpty() {
        comments = Collections.emptyList();

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(comments, result);
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testDeleteCommentWhenCommentExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID));
        verify(commentRepository).existsById(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void teatDeleteCommentWhenCommentNoExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(false);
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(COMMENT_ID));
        verify(commentRepository).existsById(COMMENT_ID);
        verify(commentRepository, never()).deleteById(COMMENT_ID);
    }
}
