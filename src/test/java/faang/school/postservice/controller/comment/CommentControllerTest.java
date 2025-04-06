package faang.school.postservice.controller.comment;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    public class CommentControllerTest {

        @Mock
        private CommentService commentService;

        @InjectMocks
        private CommentController commentController;

        private CommentDto commentDto;
        private final Long postId = 1L;
        private final Long commentId = 1L;

        @BeforeEach
        void setUp() {
            commentDto = CommentDto.builder()
                    .id(commentId)
                    .content("Test comment")
                    .authorId(1L)
                    .postId(1L)
                    .build();
        }

        @Test
        void testCreateComment() {
            when(commentService.createComment(postId, commentDto)).thenReturn(commentDto);

            ResponseEntity<CommentDto> response = commentController.createComment(postId, commentDto);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertEquals(commentDto, response.getBody());
            verify(commentService, times(1)).createComment(postId, commentDto);
        }

        @Test
        void testUpdateComment() {
            when(commentService.updateComment(commentId, commentDto)).thenReturn(commentDto);

            ResponseEntity<CommentDto> response = commentController.updateComment(commentId, commentDto);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertEquals(commentDto, response.getBody());
            verify(commentService, times(1)).updateComment(commentId, commentDto);
        }

        @Test
        void testGetCommentPostId() {
            List<CommentDto> comments = List.of(commentDto);
            when(commentService.getAllComments(postId)).thenReturn(comments);

            ResponseEntity<List<CommentDto>> response = commentController.getCommentPostId(postId);

            assertNotNull(response);
            assertEquals(200, response.getStatusCode().value());
            assertEquals(comments, response.getBody());
            verify(commentService, times(1)).getAllComments(postId);
        }

        @Test
        void testDeleteComment() {
            doNothing().when(commentService).deleteComment(commentId);

            ResponseEntity<Void> response = commentController.deleteComment(commentId);

            assertNotNull(response);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(commentService, times(1)).deleteComment(commentId);
        }
    }
