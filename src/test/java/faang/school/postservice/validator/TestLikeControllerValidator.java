package faang.school.postservice.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TestLikeControllerValidator {

    @InjectMocks
    private LikeControllerValidator validator;

    @Test
    public void testPostWhenValid() {
        long postId = 1;
        assertDoesNotThrow(() -> validator.validAddLikeToPost(postId));
    }

    @Test
    public void testPostWhenPostIdNegative() {
        long postId = 0;
        assertThrows(IllegalArgumentException.class, () -> validator.validAddLikeToPost(postId));
    }

    @Test
    public void testCommentWhenValid() {
        long commentId = 1;
        assertDoesNotThrow(() -> validator.validAddLikeToComment(commentId));
    }

    @Test
    public void testCommentWhenCommentIdNegative() {
        long commentId = 0;
        assertThrows(IllegalArgumentException.class, () -> validator.validAddLikeToComment(commentId));
    }

}
