package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentValidator commentValidator;

    private long rightId;
    private long wrongId;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        rightId = 1L;
        wrongId = -2L;
        userDto = new UserDto(rightId, "any", "any");

        Mockito.when(postRepository.findById(rightId))
                .thenReturn(Optional.of(new Post()));
        Mockito.when(userServiceClient.getUser(rightId))
                .thenReturn(userDto);
    }

    @Test
    void testIdValidator() {
        assertDoesNotThrow(() -> commentValidator.idValidator(rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.idValidator(wrongId));
    }

    @Test
    void testCommentDtoValidator() {
        CommentDto commentDto = new CommentDto(rightId, rightId, rightId, "any content", LocalDateTime.now());
        assertDoesNotThrow(() -> commentValidator.commentDtoValidator(commentDto));

        commentDto.setContent("");
        assertThrows(DataValidationException.class,
                () -> commentValidator.commentDtoValidator(commentDto));
    }

    @Test
    void testPostExistValidator() { //uses or overrides a deprecated API.
        assertDoesNotThrow(() -> commentValidator.postExistValidator(rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.postExistValidator(wrongId));
    }

    @Test
    void authorExistValidator() {
        assertDoesNotThrow(() -> commentValidator.authorExistValidator(rightId));
        assertThrows(DataValidationException.class,
                () -> commentValidator.authorExistValidator(wrongId));
    }
}