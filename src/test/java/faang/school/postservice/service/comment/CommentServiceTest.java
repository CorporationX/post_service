package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
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

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidation commentValidation;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Comment commentNew;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        commentNew = new Comment();
        commentNew.setId(COMMENT_ID);
    }

    @Test
    void testCreateCommentWhenCommentCreated() {
        doNothing().when(commentValidation).validateLengthContentComment(comment);
        doNothing().when(commentValidation).validateAuthorExists(comment);
        doNothing().when(commentValidation).validatePostExists(comment);
        when(commentRepository.save(comment)).thenReturn(commentNew);

        Comment result = commentService.createComment(comment);

        assertEquals(COMMENT_ID, result.getId());
        verify(commentValidation).validateLengthContentComment(comment);
        verify(commentValidation).validateAuthorExists(comment);
        verify(commentValidation).validatePostExists(comment);
        verify(commentRepository).save(comment);
    }

    @Test
    void testUpdateCommentWhenCommentUpdate() {
        comment.setContent("Comment");
        comment.setId(COMMENT_ID);
        commentNew.setContent("New Content");

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentValidation).validateLengthContentComment(commentNew);
        doNothing().when(commentValidation).validateAuthorExists(commentNew);
        doNothing().when(commentValidation).validatePostExists(comment);
        when(commentRepository.save(commentNew)).thenReturn(commentNew);

        Comment result = commentService.updateComment(commentNew);

        assertEquals(commentNew.getId(), result.getId());
        assertEquals(commentNew.getContent(), result.getContent());
        assertEquals(comment.getId(), result.getId());
        verify(commentValidation).validateLengthContentComment(commentNew);
        verify(commentValidation).validateAuthorExists(commentNew);
        verify(commentValidation).validatePostExists(comment);
        verify(commentRepository).save(commentNew);
    }

    @Test
    void testUpdateCommentWhenCommentNotExists() {
        commentNew.setContent("New Content");
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(commentNew));
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidation, never()).validateLengthContentComment(any());
        verify(commentValidation, never()).validateAuthorExists(any());
        verify(commentValidation, never()).validatePostExists(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testGetAllCommentWhenPostExists() {
        List<Comment> comments = List.of(comment, commentNew);

        doNothing().when(commentValidation).validatePostExistsById(POST_ID);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(comments.get(0).getId(), result.get(0).getId());
        assertEquals(comments.get(1).getId(), result.get(1).getId());
        assertEquals(comments.size(), result.size());

        verify(commentValidation).validatePostExistsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }


    @Test
    void testGetAllCommentWhenListCommentsEmpty() {
        doNothing().when(commentValidation).validatePostExistsById(POST_ID);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(Collections.emptyList());

        List<Comment> result = commentService.getAllComments(POST_ID);

        assertEquals(Collections.emptyList(), result);
        verify(commentValidation).validatePostExistsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testDeleteCommentWhenCommentExists() {
        doNothing().when(commentValidation).validateCommentExists(COMMENT_ID);
        doNothing().when(commentRepository).deleteById(COMMENT_ID);

        assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID));
        verify(commentValidation).validateCommentExists(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }
}
