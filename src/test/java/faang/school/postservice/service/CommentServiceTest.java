package faang.school.postservice.service;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    void testGetById() {
        long id = 1L;
        Comment comment = Comment.builder()
                .id(id)
                .build();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment result = commentService.getById(id);

        assertEquals(comment, result);
        verify(commentRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_notExists_throws() {
        long id = 1L;

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getById(id));
        verify(commentRepository, times(1)).findById(id);
    }

}