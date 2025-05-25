package faang.school.postservice.validation.post;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostValidationTest {

    private final Post failPost = Post.builder().authorId(null).content(null).published(true).deleted(true).build();

    @Test
    public void testValidateNotNullAuthor() {
        assertThrows(DataValidationException.class,
                () -> PostValidation.validateNotNullAuthor(failPost));
    }

    @Test
    public void testValidateNotNullContent() {
        assertThrows(DataValidationException.class,
                () -> PostValidation.validateNotNullContent(failPost));
    }

    @Test
    public void testValidateNotAlreadyPublishedPost() {
        assertThrows(DataValidationException.class,
                () -> PostValidation.validateNotAlreadyPublishedPost(failPost));
    }

    @Test
    public void testValidateNotAlreadyDeletedPost() {
        assertThrows(DataValidationException.class,
                () -> PostValidation.validateNotAlreadyDeletedPost(failPost));
    }

    @Test
    public void testValidatePostDoesNotExist() {
        boolean existPost = false;
        assertThrows(DataValidationException.class,
                () -> PostValidation.validatePostDoesNotExist(existPost));
    }

}
