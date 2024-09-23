package faang.school.postservice.controller;

import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private CommentDto commentDto = new CommentDto();
    private CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
    private Long postId = 1L;
    private Long commentId = 1L;

    @Test
    void create_emptyComment_throwsException() {
        commentDto.setContent("");
        assertThrows(DataValidationException.class,
                () -> commentController.create(postId, commentDto));
    }

    @Test
    void create_commentIsTooLarge_throwsException() {
        commentDto.setContent("a".repeat(4097));
        assertThrows(DataValidationException.class,
                () -> commentController.create(postId, commentDto));
    }

    @Test
    void create_validComment_serviceCreateCalled() {
        commentDto.setContent("content");
        commentController.create(postId, commentDto);
        verify(commentService, times(1))
                .create(postId, commentDto);
    }

    @Test
    void update_emptyComment_throwsException() {
        commentUpdateDto.setContent("");
        assertThrows(DataValidationException.class,
                () -> commentController.update(postId, commentUpdateDto));
    }

    @Test
    void update_commentIsTooLarge_throwsException() {
        commentUpdateDto.setContent("a".repeat(4097));
        assertThrows(DataValidationException.class,
                () -> commentController.update(postId, commentUpdateDto));
    }

    @Test
    void update_validComment_serviceUpdateCalled() {
        commentUpdateDto.setContent("content");
        commentController.update(postId, commentUpdateDto);
        verify(commentService, times(1))
                .update(postId, commentUpdateDto);
    }

    @Test
    void getByPostId_validRequest_serviceGetByPostIdCalled() {
        commentController.getByPostId(postId);
        verify(commentService, times(1)).getByPostId(postId);
    }

    @Test
    void delete_validRequest_serviceDeleteCalled() {
        commentController.delete(commentId);
        verify(commentService, times(1)).delete(commentId);
    }
}