package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private CommentService commentService;
    private long rightId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateComment() {
        CommentDto commentDto = CommentDto.builder().build();
        commentController.updateComment(commentDto);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validatorCommentDto(commentDto);
        Mockito.verify(commentService, Mockito.times(1))
                .updateComment(commentDto);
    }

    @Test
    void testDeleteComment() {
        commentController.getAllComments(rightId);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validatorPostExist(rightId);
        Mockito.verify(commentService, Mockito.times(1))
                .getAllComments(rightId);
    }
}