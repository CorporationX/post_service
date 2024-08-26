package faang.school.postservice.validator;

import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    @DisplayName("testing validatePostExistence method with non appropriate value")
    void testValidatePostExistenceWithNonAppropriateValue() {
        long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> postValidator.validatePostExistence(postId));
    }

    @Test
    @DisplayName("testing validatePostExistence method with appropriate value")
    void testValidatePostExistenceWithAppropriateValue() {
        long postId = 1L;
        when(postRepository.existsById(postId)).thenReturn(true);
        assertDoesNotThrow(() -> postValidator.validatePostExistence(postId));
    }
}