package faang.school.postservice.controller;

import faang.school.postservice.controller.comment.CommentController;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private final Long postId = 1L;
    private final Long commentId = 2L;

    @Test
    void testAddCommentValidDtoReturnsCreated() {
        CommentDto commentDto = CommentDto.builder()
                .content("Valid comment content")
                .authorId(123L)
                .build();

        CommentDto expectedCommentDto = CommentDto.builder()
                .id(3L) // Assuming service assigns ID
                .content("Valid comment content")
                .authorId(123L)
                .postId(postId)
                .build();

        when(commentService.addComment(any(CommentDto.class))).thenReturn(expectedCommentDto);

        ResponseEntity<CommentDto> response = commentController.addComment(postId, commentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedCommentDto, response.getBody());

        ArgumentCaptor<CommentDto> commentCaptor = ArgumentCaptor.forClass(CommentDto.class);
        verify(commentService).addComment(commentCaptor.capture());

        CommentDto capturedComment = commentCaptor.getValue();
        assertEquals(postId, capturedComment.getPostId()); // Verify postId is set
        assertEquals(commentDto.getContent(), capturedComment.getContent()); // Verify content is same
        assertEquals(commentDto.getAuthorId(), capturedComment.getAuthorId()); // Verify authorId is same
    }

    @Test
    void testAddCommentInvalidDtoReturnsBadRequest() {
        CommentDto invalidCommentDto = CommentDto.builder().content("  ").build(); // Empty content

        ResponseEntity<CommentDto> response = commentController.addComment(postId, invalidCommentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(commentService);
    }

    @Test
    void testUpdateCommentValidDtoReturnsOk() {
        CommentDto commentDto = CommentDto.builder()
                .content("Updated content")
                .authorId(456L)
                .id(commentId)
                .build();

        CommentDto updatedCommentDto = CommentDto.builder()
                .id(commentId)
                .content("Updated content")
                .authorId(456L)
                .postId(postId)
                .build();

        when(commentService.updateComment(any(CommentDto.class))).thenReturn(updatedCommentDto);

        ResponseEntity<CommentDto> response = commentController.updateComment(postId, commentId, commentDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedCommentDto, response.getBody());

        ArgumentCaptor<CommentDto> commentCaptor = ArgumentCaptor.forClass(CommentDto.class);
        verify(commentService).updateComment(commentCaptor.capture());

        CommentDto capturedComment = commentCaptor.getValue();
        assertEquals(commentId, capturedComment.getId()); // Verify commentId is set
        assertEquals(commentDto.getContent(), capturedComment.getContent()); // Verify content is the same
    }

    @Test
    void testUpdateCommentInvalidDtoReturnsBadRequest() {
        CommentDto invalidCommentDto = CommentDto.builder().content("   ").build(); // Empty content

        ResponseEntity<CommentDto> response = commentController.updateComment(postId, commentId, invalidCommentDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(commentService);
    }

    @Test
    void testGetAllCommentsReturnsOk() {
        CommentDto comment1 = CommentDto.builder().id(1L).content("Comment 1").authorId(123L).postId(postId).build();
        CommentDto comment2 = CommentDto.builder().id(2L).content("Comment 2").authorId(456L).postId(postId).build();
        List<CommentDto> comments = List.of(comment1, comment2);

        when(commentService.getAllComments(postId)).thenReturn(comments);

        ResponseEntity<List<CommentDto>> response = commentController.getAllComments(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(comments, response.getBody());
        verify(commentService).getAllComments(postId);
    }

    @Test
    void testGetAllCommentsNoCommentsReturnsOkWithEmptyList() {
        when(commentService.getAllComments(postId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<CommentDto>> response = commentController.getAllComments(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(commentService).getAllComments(postId);
    }

    @Test
    void testDeleteCommentReturnsNoContent() {
        ResponseEntity<Void> response = commentController.deleteComment(postId, commentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(commentService).deleteComment(commentId);
    }
}
