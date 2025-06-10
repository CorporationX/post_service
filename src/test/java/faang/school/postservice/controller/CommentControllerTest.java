package faang.school.postservice.controller;

import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentDto validCommentDto;
    private CommentDto invalidCommentDto;
    private Long postId;
    private Long commentId;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 2L;

        validCommentDto = CommentDto.builder()
                .content("Valid comment content")
                .authorId(123L)
                .postId(postId)
                .build();

        invalidCommentDto = CommentDto.builder()
                .content("   ")
                .build();
    }

    @Test
    void testAddComment_ValidDto_ReturnsCreated() {
        when(commentService.addComment(any(CommentDto.class))).thenReturn(validCommentDto);

        ResponseEntity<CommentDto> response = commentController.addComment(postId, validCommentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validCommentDto, response.getBody());
        verify(commentService).addComment(any(CommentDto.class));
    }

    @Test
    void testAddComment_InvalidDto_ReturnsBadRequest() {
        ResponseEntity<CommentDto> response = commentController.addComment(postId, invalidCommentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(commentService);
    }

    @Test
    void testUpdateComment_ValidDto_ReturnsOk() {
        when(commentService.updateComment(any(CommentDto.class))).thenReturn(validCommentDto);

        ResponseEntity<CommentDto> response = commentController.updateComment(postId, commentId, validCommentDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(validCommentDto, response.getBody());
        verify(commentService).updateComment(any(CommentDto.class));
    }

    @Test
    void testUpdateComment_InvalidDto_ReturnsBadRequest() {
        ResponseEntity<CommentDto> response = commentController.updateComment(postId, commentId, invalidCommentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(commentService);
    }

    @Test
    void testGetAllComments_ReturnsOk() {
        List<CommentDto> comments = Collections.singletonList(validCommentDto);
        when(commentService.getAllComments(postId)).thenReturn(comments);

        ResponseEntity<List<CommentDto>> response = commentController.getAllComments(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(comments, response.getBody());
        verify(commentService).getAllComments(postId);
    }

    @Test
    void testDeleteComment_ReturnsNoContent() {
        ResponseEntity<Void> response = commentController.deleteComment(postId, commentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commentService).deleteComment(commentId);
    }
}