package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 1L;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        comment = new Comment();
    }

//    @Test
//    void createComment() {
//        when(commentRepository.save(comment)).thenReturn();
//        //todo argumentCatcher
//        //ArgumentCaptor
//    }

    @Test
    void updateComment() {
    }

    @Test
    void testGetAllCommentWhenPostExists() {
        comments = List.of(comment);

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComment(POST_ID);

        assertEquals(comments, result);
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testGetAllCommentWhenPostNoExists() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllComment(POST_ID));
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository, never()).findAllByPostId(POST_ID);
    }

    @Test
    void testGetAllCommentWhenListCommentsEmpty() {
        comments = Collections.emptyList();

        when(postRepository.existsById(POST_ID)).thenReturn(true);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getAllComment(POST_ID);

        assertEquals(comments, result);
        verify(postRepository).existsById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testDeleteCommentWhenCommentExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(true);

        assertDoesNotThrow(() -> commentService.deleteComment(COMMENT_ID));
        verify(commentRepository).existsById(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void teatDeleteCommentWhenCommentNoExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(COMMENT_ID));
        verify(commentRepository).existsById(COMMENT_ID);
        verify(commentRepository, never()).deleteById(COMMENT_ID);
    }

    @Test
    void testGetCommentWhenCommentNoExists() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.getComment(COMMENT_ID));
        verify(commentRepository).findById(COMMENT_ID);
    }
}
