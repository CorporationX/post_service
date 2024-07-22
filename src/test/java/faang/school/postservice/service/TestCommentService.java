package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestCommentService {

    @InjectMocks
    private CommentService service;

    @Mock
    private CommentRepository repository;

    @Test
    public void testGetCommentNotCommentDataBase() {
        long commentId = 1;
        when(repository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.getComment(commentId));
    }

    @Test
    public void testGetCommentWhenValid() {
        long commentId = 1;
        Comment comment = new Comment();
        when(repository.findById(commentId)).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> service.getComment(commentId));
    }

}