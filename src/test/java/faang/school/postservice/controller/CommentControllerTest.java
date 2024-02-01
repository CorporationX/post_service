package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @Mock
    private CommentService commentService;
    @Mock
    private CommentValidator commentValidator;
    @InjectMocks
    private CommentController commentController;

    @Test
    void testCreateCommentShouldCallServiceMethod() {
        var postId = 1L;
        var commentDto = CommentDto.builder().content("content").build();
        commentController.createComment(postId, commentDto);
        verify(commentService,times(1)).createComment(postId,commentDto);
    }

    @Test
    void testUpdateCommentShouldCallServiceMethod() {
        var postId = 1L;
        var commentId = 1L;
        var commentEditDto = CommentEditDto.builder().content("content").build();
        commentController.updateComment(postId, commentId, commentEditDto);
        verify(commentService, times(1)).updateComment(postId, commentId, commentEditDto);
    }

    @Test
    void testGetCommentsShouldCallServiceMethod() {
        var postId = 1L;
        commentController.getCommentsByPostId(postId);
        verify(commentService, times(1)).getCommentsByPostId(postId);
    }

    @Test
    void testDeleteCommentShouldCallServiceMethod() {
        var postId = 1L;
        var commentId = 1L;
        commentController.deleteComment(postId, commentId);
        verify(commentService, times(1)).deleteComment(postId, commentId);
    }
}