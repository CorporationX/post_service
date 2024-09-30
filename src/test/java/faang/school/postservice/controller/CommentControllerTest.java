package faang.school.postservice.controller;

import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.Assert.assertEquals;
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
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }


    @Test
    void createComment_Comment_emptyComment_throwsException() {
        commentDto.setContent("");
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertEquals(1, violations.size());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void createComment_Comment_commentIsTooLarge_throwsException() {
        commentDto.setContent("a".repeat(4097));
        Set<ConstraintViolation<CommentDto>> violations = validator.validate(commentDto);
        assertEquals(1, violations.size());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void createComment_validComment_serviceCreateCommentCalled() {
        commentDto.setContent("content");
        commentController.createComment(postId, commentDto);
        verify(commentService, times(1))
                .createComment(postId, commentDto);
    }

    @Test
    void updateComment_Comment_emptyComment_throwsException() {
        commentUpdateDto.setContent("");
        Set<ConstraintViolation<CommentUpdateDto>> violations = validator.validate(commentUpdateDto);
        assertEquals(1, violations.size());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void updateComment_Comment_commentIsTooLarge_throwsException() {
        commentUpdateDto.setContent("a".repeat(4097));
        Set<ConstraintViolation<CommentUpdateDto>> violations = validator.validate(commentUpdateDto);
        assertEquals(1, violations.size());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void updateComment_validComment_serviceUpdateCommentCalled() {
        commentUpdateDto.setContent("content");
        commentController.updateComment(postId, commentUpdateDto);
        verify(commentService, times(1))
                .updateComment(postId, commentUpdateDto);
    }

    @Test
    void getComments_validRequest_serviceGetCommentsCalled() {
        commentController.getComments(postId);
        verify(commentService, times(1)).getComments(postId);
    }

    @Test
    void deleteComment_validRequest_serviceDeleteCommentCalled() {
        commentController.deleteComment(commentId);
        verify(commentService, times(1)).deleteComment(commentId);
    }
}