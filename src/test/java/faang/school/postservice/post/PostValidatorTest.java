package faang.school.postservice.post;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostValidator postValidator;
    private Post post;
    private Long authorId;
    private Long projectId;

    @BeforeEach
    void setUp() {
        post = new Post();
        authorId = 1L;
        projectId = 2L;
    }

    @Test
    public void testValidateAuthorIdAndProjectIdUserDoesNotExist() {
        when(postRepository.existsById(authorId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testValidateAuthorIdAndProjectIdProjectDoesNotExist() {
        when(postRepository.existsById(authorId)).thenReturn(true);
        when(postRepository.existsById(projectId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testValidateAuthorIdAndProjectIdProject() {
        when(postRepository.existsById(authorId)).thenReturn(true);
        when(postRepository.existsById(projectId)).thenReturn(true);

        assertDoesNotThrow(() -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
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
}