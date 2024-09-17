package faang.school.postservice.validator.comment;

import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceValidatorTest {
    private final int MAX_CONTENT_LENGTH = 4096;
    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceValidator commentServiceValidator;

    @Test
    public void validateCommentExist() {
        Long commentId = 10L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        assertDoesNotThrow(() -> commentServiceValidator.validateCommentExist(commentId));
    }

    @Test
    public void validateCommentNotExist() {
        Long commentId = 10L;
        when(commentRepository.existsById(commentId)).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> commentServiceValidator.validateCommentExist(commentId));

        assertEquals("Comment with id " + commentId + " does not exist", exception.getMessage());
    }

    @Test
    void validatePostExist() {

    }

    @Test
    void validateCommentContentSuccess() {
        assertDoesNotThrow(() -> commentServiceValidator.validateCommentContent("Some content"));
    }

    @Test
    void validateCommentContentEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentServiceValidator.validateCommentContent(""));
        assertEquals("Comment content is too long or content is empty", exception.getMessage());
    }

    @Test
    void validateCommentContentToLong() {
        String content = RandomStringUtils.random(MAX_CONTENT_LENGTH + 1, true, true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> commentServiceValidator.validateCommentContent(content));
        assertEquals("Comment content is too long or content is empty", exception.getMessage());
    }
}