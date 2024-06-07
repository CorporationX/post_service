package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @InjectMocks
    private CommentValidator commentValidator;

    @Mock
    private PostRepository postRepository;

    private Long postId;

    @BeforeEach
    public void setUp(){
        postId = 1L;
    }

    @Test
    public void testCorrectWorkGetAllCommentsOnPostIdService() {
        when(postRepository.existsById(postId)).thenReturn(true);
        assertDoesNotThrow(() -> commentValidator.getAllCommentsOnPostIdService(postId));
    }

    @Test
    public void testGetAllCommentsOnPostIdServiceWithUnAttendedUserInDB() {
        when(postRepository.existsById(postId)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> commentValidator.getAllCommentsOnPostIdService(postId));
    }
}