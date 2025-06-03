package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void getUserByIdTest() {
        long id = 1L;
        Comment comment = Comment.builder().id(id).build();

        Mockito.when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getUserByIdTestException() {
        long id = -1L;
        Mockito.when(commentRepository.findById(id))
                .thenThrow(new IllegalArgumentException("There is no such id = " + id));

        assertThrows(IllegalArgumentException.class, () -> commentRepository.findById(id));
    }
}