package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    private static final String MESSAGE_COMMENT_IS_NULL = "Comment is null";
    private static final String MESSAGE_INVALID_COMMENT_ID = "Invalid commentId";
    private static final String MESSAGE_INVALID_POST_ID = "Invalid postId";
    private static final long INVALID_ID = -1L;
    private static final long VALID_ID = 3L;
    @Mock
    private CommentService service;
    @InjectMocks
    private CommentController controller;

    @Test
    public void testInvalidPostId() {
        assertEquals(MESSAGE_INVALID_POST_ID,
                assertThrows(RuntimeException.class,
                        () -> controller.addComment(INVALID_ID, new CommentDto())).getMessage());
    }

    @Test
    public void testDtoIsNull() {
        assertEquals(MESSAGE_COMMENT_IS_NULL,
                assertThrows(RuntimeException.class,
                        () -> controller.addComment(VALID_ID, null)).getMessage());
    }

    @Test
    public void testVerifyServiceAddComment() {
        controller.addComment(VALID_ID, new CommentDto());
        Mockito.verify(service).addComment(VALID_ID, new CommentDto());
    }

    @Test
    public void testVerifyServiceChangeComment() {
        controller.changeComment(VALID_ID, new CommentDto());
        Mockito.verify(service).changeComment(VALID_ID, new CommentDto());
    }

    @Test
    public void testVerifyServiceGetAllCommentsOfPost() {
        controller.getAllCommentsOfPost(VALID_ID);
        Mockito.verify(service).getAllCommentsOfPost(VALID_ID);
    }

    @Test
    public void testInvalidCommentId() {
        assertEquals(MESSAGE_INVALID_COMMENT_ID,
                assertThrows(RuntimeException.class,
                        () -> controller.deleteComment(VALID_ID, INVALID_ID)).getMessage());
    }

    @Test
    public void testVerifyServiceDeleteComment() {
        controller.deleteComment(VALID_ID, VALID_ID);
        Mockito.verify(service).deleteComment(VALID_ID, VALID_ID);
    }


}