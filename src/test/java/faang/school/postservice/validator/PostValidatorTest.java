package faang.school.postservice.validator;

import static org.junit.jupiter.api.Assertions.*;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    void shouldValidateAuthorIdAndProjectIdBothNullThrowsException() {
        assertThrows(DataValidationException.class, () ->
                postValidator.validateAuthorIdAndProjectId(null, null));
    }

    @Test
    void shouldValidateAuthorIdAndProjectIdBothProvidedThrowsException() {
        assertThrows(DataValidationException.class, () ->
                postValidator.validateAuthorIdAndProjectId(1L, 1L));
    }

    @Test
    void shouldValidateAuthorIdAndProjectIdValidAuthorIdNoException() {
        when(postRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> postValidator.validateAuthorIdAndProjectId(1L, null));
    }

    @Test
    void shouldValidateAuthorIdAndProjectIdInvalidAuthorIdThrowsException() {
        when(postRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () ->
                postValidator.validateAuthorIdAndProjectId(1L, null));
    }

    @Test
    void ShouldValidateAuthorIdAndProjectIdValidProjectIdNoException() {
        when(postRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> postValidator.validateAuthorIdAndProjectId(null, 1L));
    }

    @Test
    void shouldValidateAuthorIdAndProjectIdInvalidProjectIdThrowsException() {
        when(postRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () ->
                postValidator.validateAuthorIdAndProjectId(null, 1L));
    }

    @Test
    void shouldValidatePublicationPostNotPublishedNoException() {
        Post post = new Post();
        post.setPublished(false);
        assertDoesNotThrow(() -> postValidator.validatePublicationPost(post));
    }

    @Test
    void shouldValidatePublicationPostAlreadyPublishedThrowsException() {
        Post post = new Post();
        post.setPublished(true);
        assertThrows(DataValidationException.class, () ->
                postValidator.validatePublicationPost(post));
    }
}