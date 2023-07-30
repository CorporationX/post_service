package faang.school.postservice.service.comment;

import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private CommentRepository commentRepository;
    private long rightId;
    private long wrongId;
    private Comment comment = new Comment();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        rightId = 1L;
        wrongId = -2L;
    }

    @Test
    void createComment() {
    }

    @Test
    void updateComment() {
    }

    @Test
    void getAllComments() {
    }

    @Test
    void testDeleteComment() {
        commentService.deleteComment(rightId, rightId);

        Mockito.verify(commentValidator, Mockito.times(1))
                .deleteCommentValidator(rightId, rightId);
        Mockito.verify(commentRepository, Mockito.times(1))
                .deleteById(rightId);
    }

    @Test
    void testGetCommentById() {
        Mockito.when(commentRepository.findById(rightId))
                .thenReturn(Optional.ofNullable(comment));

        commentService.getCommentById(rightId);

        assertDoesNotThrow(() -> commentService.getCommentById(rightId));
        assertThrows(DataValidationException.class,
                () -> commentService.getCommentById(wrongId));
    }
}