package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @InjectMocks
    private CommentValidator commentValidator;

    @Mock
    private PostRepository postRepository;

    private String content;
    private Long authorId;
    private Long postId;
    private Long commentId;

    @BeforeEach
    public void setUp(){
        content = "content";
        authorId = 1L;
        postId = 1L;
        commentId = 1L;
    }

    @Test
    public void testCorrectWorkCreateCommentController() {
        assertDoesNotThrow(() -> commentValidator.createCommentController(content,authorId, postId));
    }

    @Test
    public void testCreateCommentControllerWithNullContent() {
        content = null;
        assertThrows(DataValidationException.class, () -> commentValidator.createCommentController(content,authorId, postId));
    }

    @Test
    public void testCreateCommentControllerWithEmptyContent() {
        content = " ";
        assertThrows(DataValidationException.class, () -> commentValidator.createCommentController(content,authorId, postId));
    }

    @Test
    public void testCreateCommentControllerWithNullPostId() {
        postId = null;
        assertThrows(DataValidationException.class, () -> commentValidator.createCommentController(content,authorId, postId));
    }

    @Test
    public void testCreateCommentControllerWithBiggerNumberOfSymbols() {
        content = "0".repeat(4097);
        assertThrows(DataValidationException.class, () -> commentValidator.createCommentController(content,authorId, postId));
    }

    @Test
    public void testCorrectWorkChangeCommentController() {
        assertDoesNotThrow(() -> commentValidator.changeCommentController(commentId, content));
    }

    @Test
    public void testChangeCommentControllerWithNullContent() {
        content = null;
        assertThrows(DataValidationException.class, () -> commentValidator.changeCommentController(commentId, content));
    }

    @Test
    public void testChangeCommentControllerWithEmptyContent() {
        content = " ";
        assertThrows(DataValidationException.class, () -> commentValidator.changeCommentController(commentId, content));
    }

    @Test
    public void testChangeCommentControllerWithNullContentId() {
        commentId = null;
        assertThrows(DataValidationException.class, () -> commentValidator.changeCommentController(commentId, content));
    }

    @Test
    public void testCorrectWorkGetAllCommentsOnPostIdController() {
        assertDoesNotThrow(() -> commentValidator.getAllCommentsOnPostIdController(postId));
    }

    @Test
    public void testGetAllCommentsOnPostIdControllerWithNullPostId() {
        postId = null;
        assertThrows(DataValidationException.class, () -> commentValidator.getAllCommentsOnPostIdController(postId));
    }

    @Test
    public void testCorrectWorkDeleteCommentController() {
        assertDoesNotThrow(() -> commentValidator.deleteCommentController(commentId));
    }

    @Test
    public void testDeleteCommentControllerWithNullContentId() {
        commentId = null;
        assertThrows(DataValidationException.class, () -> commentValidator.deleteCommentController(commentId));
    }

    @Test
    public void testCorrectWorkGetAllCommentsOnPostIdService() {
        when(postRepository.existsById(postId)).thenReturn(true);
        assertDoesNotThrow(() -> commentValidator.getAllCommentsOnPostIdService(postId));
    }

    @Test
    public void testGetAllCommentsOnPostIdServiceWithUnAttendedUserInDB() {
        when(postRepository.existsById(postId)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> commentValidator.getAllCommentsOnPostIdService(postId));
    }
}