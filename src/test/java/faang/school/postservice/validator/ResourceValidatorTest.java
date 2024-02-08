package faang.school.postservice.validator;

import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ResourceValidatorTest {
    private ResourceValidator resourceValidator = new ResourceValidator();

    @Test
    void testValidateResourceLimitWhenCountIsGreaterThan10() {
        assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceLimit(11);
        });
    }

    @Test
    void testValidateResourceBelongsToPostWhenResourceDoesNotBelongsToPost() {
        Post post = Post.builder().id(2L).build();
        Resource resource = Resource.builder().post(post).build();
        assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateResourceBelongsToPost(resource, 1L);
        });
    }

    @Test
    void testValidateUserIsPostAuthorWhenUserIsNotPostAuthor() {
        assertThrows(DataValidationException.class, () -> {
            resourceValidator.validateUserIsPostAuthor(1L, 2L);
        });
    }
}