package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClientMock;
import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private UserServiceClientMock userServiceClient;
    @Mock
    private ProjectServiceClientMock projectServiceClient;
    @InjectMocks
    private PostValidator postValidator;

    @Test
    void testValidateCreateAuthorAndProject() {
        Post createPost = Post.builder()
                .authorId(1L)
                .projectId(2L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));
    }

    @Test
    void testValidateCreateEmptyAuthorAndProject() {
        Post createPost = Post.builder()
                .authorId(null)
                .projectId(null)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));
    }

    @Test
    void testValidateCreateInputAuthor() {
        Post createPost = Post.builder()
                .authorId(1L)
                .build();

        when(userServiceClient
                .getUser(createPost.getAuthorId()))
                .thenReturn(new UserDto(createPost.getAuthorId(), null, null));

        postValidator.validateCreatePost(createPost);

        verify(userServiceClient).getUser(createPost.getAuthorId());
    }

    @Test
    void testValidateCreateInputAuthorFailed() {
        Post createPost = Post.builder()
                .authorId(1L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));

        verify(userServiceClient).getUser(createPost.getAuthorId());
    }

    @Test
    void testValidateCreateInputProject() {
        Post createPost = Post.builder()
                .projectId(1L)
                .build();

        when(projectServiceClient
                .getProject(createPost.getProjectId()))
                .thenReturn(new ProjectDto());

        postValidator.validateCreatePost(createPost);

        verify(projectServiceClient).getProject(createPost.getProjectId());
    }

    @Test
    void testValidateCreateInputProjectFailed() {
        Post createPost = Post.builder()
                .projectId(1L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));

        verify(projectServiceClient).getProject(createPost.getProjectId());
    }
}
