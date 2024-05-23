package faang.school.postservice.post;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @InjectMocks
    private PostValidator postValidator;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
    }

    @Test
    public void testCorrectValidateContentWith() {
        String content = "text";
        assertDoesNotThrow(() -> postValidator.validatePostContent(content));
    }

    @Test
    public void testValidateContentWithNull() {
        String content = null;
        assertThrows(DataValidationException.class, () -> postValidator.validatePostContent(content));
    }

    @Test
    public void testValidateContentWithEmpty() {
        String content = "   ";
        assertThrows(DataValidationException.class, () -> postValidator.validatePostContent(content));
    }

    @Test
    public void testCorrectValidateAuthorIdAndProjectId() {
        Long authorId = 1L;
        Long projectId = 2L;
        assertDoesNotThrow(() -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testValidateAuthorIdAndProjectIdWithNull() {
        Long authorId = null;
        Long projectId = null;
        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testCorrectValidatePublicationPost() {
        post.setPublished(false);
        assertDoesNotThrow(() -> postValidator.validatePublicationPost(post));
    }

    @Test
    public void testValidatePublicationPostWithTrue() {
        post.setPublished(true);
        assertThrows(DataValidationException.class, () -> postValidator.validatePublicationPost(post));
    }

    @Test
    public void testCorrectValidateId() {
        Long id = 1L;
        assertDoesNotThrow(() -> postValidator.validateId(id));
    }

    @Test
    public void testValidateIdWithNull() {
        Long id = null;
        assertThrows(DataValidationException.class, () -> postValidator.validateId(id));
    }
}