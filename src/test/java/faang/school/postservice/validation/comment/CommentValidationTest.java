package faang.school.postservice.validation.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidationTest {
    private static final long POST_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long COMMENT_ID = 3L;
    private static final String CONTENT = "content";

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;


    @InjectMocks
    private CommentValidation commentValidation;

    @Test
    void testValidateLengthContentCommentWhenContentEmpty() {
        String emptyContent = "";
        assertThrows(DataValidationException.class,
                () -> commentValidation.validateLengthContentComment(emptyContent));
    }

    @Test
    void testValidateLengthContentCommentWhenContentExists() {
        assertDoesNotThrow(() -> commentValidation.validateLengthContentComment(CONTENT));
    }

    @Test
    void testValidateLengthContentCommentWhenContentOverLength() {
        String content = "c".repeat(CommentValidation.MAX_LENGTH_CONTENT + 1);

        assertThrows(DataValidationException.class,
                () -> commentValidation.validateLengthContentComment(content));
    }

    @Test
    void testCheckAuthorEqualsUserWhenEqual() {
        assertDoesNotThrow(() -> commentValidation.checkAuthorEqualsUser(USER_ID, USER_ID));
    }

    @Test
    void testCheckAuthorEqualsUserWhenNoEqual() {
        long otherId = USER_ID + 1;

        assertThrows(DataValidationException.class,
                () -> commentValidation.checkAuthorEqualsUser(USER_ID, otherId));
    }

    @Test
    void testCheckAuthorEqualsUserWhenAuthorIdNull() {
        assertThrows(DataValidationException.class,
                () -> commentValidation.checkAuthorEqualsUser(null, USER_ID));
    }

    @Test
    void testCheckAuthorEqualsUserWhenUserIdNull() {
        assertThrows(DataValidationException.class,
                () -> commentValidation.checkAuthorEqualsUser(USER_ID, null));
    }


    @Test
    void testCheckContentNotEqualsWhenEqual() {
        assertThrows(DataValidationException.class,
                () -> commentValidation.checkContentNotEquals(CONTENT, CONTENT));
    }

    @Test
    void testValidatePostExistsWhenPostExists() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);

        assertDoesNotThrow(() -> commentValidation.validatePostExists(POST_ID));
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    void testValidatePostExistsWhenPostNoExists() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> commentValidation.validatePostExists(POST_ID));
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    void testValidatePostExistsWhenPostIsNull() {
        assertThrows(DataValidationException.class,
                () -> commentValidation.validatePostExists(null));
    }

}