package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @Mock
    private CommentValidator commentValidator;

    private CommentDto commentDto;
    private Long id;

    @BeforeEach
    public void setUp() {
        commentDto = CommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        id = 1L;
    }

    @Test
    public void testCorrectWorkCreateComment() {
        assertDoesNotThrow(() -> commentController.createComment(commentDto));
        verify(commentService).createComment(commentDto);
    }

    @Test
    public void testCreateCommentWithValidatorException() {
        doThrow(DataValidationException.class).when(commentValidator).createCommentController(commentDto.getContent(),
                commentDto.getAuthorId(), commentDto.getPostId());
        assertThrows(DataValidationException.class, () -> commentController.createComment(commentDto));
    }

    @Test
    public void testCorrectWorkChangeComment() {
        assertDoesNotThrow(() -> commentController.changeComment(commentDto));
        verify(commentService).changeComment(commentDto);
    }

    @Test
    public void testChangeCommentWithValidatorException() {
        doThrow(DataValidationException.class).when(commentValidator).changeCommentController(commentDto.getId(),
                commentDto.getContent());
        assertThrows(DataValidationException.class, () -> commentController.changeComment(commentDto));
    }

    @Test
    public void testCorrectWorkGetAllCommentsOnPostId() {
        assertDoesNotThrow(() -> commentController.getAllCommentsOnPostId(id));
        verify(commentService).getAllCommentsOnPostId(id);
    }

    @Test
    public void testGetAllCommentsOnPostIdWithValidatorException() {
        doThrow(DataValidationException.class).when(commentValidator).getAllCommentsOnPostIdController(id);
        assertThrows(DataValidationException.class, () -> commentController.getAllCommentsOnPostId(id));
    }


    @Test
    public void testCorrectWorkDeleteComment() {
        assertDoesNotThrow(() -> commentController.deleteComment(id));
        verify(commentService).deleteComment(id);
    }

    @Test
    public void testDeleteCommentWithValidatorException() {
        doThrow(DataValidationException.class).when(commentValidator).deleteCommentController(id);
        assertThrows(DataValidationException.class, () -> commentController.deleteComment(id));
    }
}
