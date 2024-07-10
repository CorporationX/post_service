package faang.school.postservice.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
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

    @Mock
    private UserServiceClient userServiceClient;

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
        when(userServiceClient.getUser(authorId)).thenReturn(null);

        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testValidateAuthorIdAndProjectIdProjectDoesNotExist() {
        when(userServiceClient.getUser(authorId)).thenReturn(UserDto.builder().build());
        when(postRepository.existsById(projectId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> postValidator.validateAuthorIdAndProjectId(authorId, projectId));
    }

    @Test
    public void testValidateAuthorIdAndProjectIdProject() {
        when(userServiceClient.getUser(authorId)).thenReturn(UserDto.builder().build());
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