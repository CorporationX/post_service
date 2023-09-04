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
    CommentDto commentDto = CommentDto
            .builder()
            .id(rightId)
            .authorId(rightId)
            .postId(rightId)
            .content("any content")
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        commentController.createComment(commentDto);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validateCommentDto(commentDto);
        Mockito.verify(commentService, Mockito.times(1))
                .createComment(commentDto);
    }

    @Test
    void testUpdateComment() {
        commentController.updateComment(commentDto);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validateCommentDto(commentDto);
        Mockito.verify(commentService, Mockito.times(1))
                .updateComment(commentDto);
    }

    @Test
    void testGetAllComments() {
        commentController.getAllComments(rightId);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validatePostExist(rightId);
        Mockito.verify(commentService, Mockito.times(1))
                .getAllComments(rightId);
    }

    @Test
    void testDeleteComment() {
        commentController.deleteComment(rightId, rightId);
        Mockito.verify(commentValidator, Mockito.times(1))
                .validateDeleteComment(rightId, rightId);
        Mockito.verify(commentService, Mockito.times(1))
                .deleteComment(rightId);
    }
}