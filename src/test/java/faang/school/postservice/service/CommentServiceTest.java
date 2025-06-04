package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comments.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void testGetComment() {
        Comment comment = new Comment();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        assertEquals(comment, commentService.getComment(1L));
    }

    @Test
    void testCommentIsNotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> commentService.getComment(1L));
        assertEquals("Comment not found!", dataValidationException.getMessage());
    }
}