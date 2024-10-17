package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.dto.project.ProjectDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @InjectMocks
    private PostValidator postValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    private PostDto postDto;
    private Post post;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .title("Title")
                .content("Content")
                .build();
        post = Post.builder()
                .authorId(1L)
                .published(false)
                .build();
    }

    @Test
    void testCreateDraftPostValidator_WhenAuthorExists() {
        // Arrange
        when(userServiceClient.getUser(1L))
                .thenReturn(new UserDto(1L, "", "", ""));

        // Act
        postValidator.createDraftPostValidator(postDto);

        // Assert
        verify(userServiceClient, times(1)).getUser(1L);
        verifyNoInteractions(projectServiceClient);
    }

    @Test
    void testCreateDraftPostValidator_WhenProjectExists() {
        // Arrange
        postDto = PostDto.builder()
                .id(1L)
                .projectId(1L)
                .title("Title")
                .content("Content")
                .build();

        when(projectServiceClient.getProject(1L))
                .thenReturn(new ProjectDto(1L, "Project Title"));

        // Act
        postValidator.createDraftPostValidator(postDto);

        // Assert
        verify(projectServiceClient, times(1)).getProject(1L);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void testCreateDraftPostValidator_ShouldThrow_WhenBothAuthorAndProjectExist() {
        // Arrange
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .build();

        // Act & Assert
        assertThrows(DataValidationException.class, () -> {
            postValidator.createDraftPostValidator(postDto);
        });
    }

    @Test
    void testCreateDraftPostValidator_ShouldThrow_WhenNeitherAuthorNorProjectExists() {
        // Arrange
        postDto = PostDto.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .build();

        // Act & Assert
        assertThrows(DataValidationException.class, () -> {
            postValidator.createDraftPostValidator(postDto);
        });
    }

    @Test
    void testPublishPostValidator_ShouldThrow_WhenAlreadyPublished() {
        // Arrange
        post.setPublished(true);

        // Act & Assert
        assertThrows(DataValidationException.class, () ->
                postValidator.publishPostValidator(post));
    }

    @Test
    void testUpdatePostValidator_ShouldThrow_WhenAuthorChanged() {
        // Arrange
        postDto = PostDto.builder()
                .authorId(2L)
                .build();

        // Act & Assert
        assertThrows(DataValidationException.class, () ->
                postValidator.updatePostValidator(post, postDto));
    }

    @Test
    void testUpdatePostValidator_ShouldThrow_WhenProjectChanged() {
        // Arrange
        post.setProjectId(1L);
        post.setAuthorId(null);

        postDto = PostDto.builder()
                .authorId(2L)
                .build();

        // Act & Assert
        assertThrows(DataValidationException.class, () ->
                postValidator.updatePostValidator(post, postDto));
    }

    @Test
    void testValidateIfAuthorExists_ShouldThrow_WhenAuthorNotFound() {
        // Arrange
        when(userServiceClient.getUser(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(DataValidationException.class, () ->
                postValidator.validateIfAuthorExists(1L));
    }

    @Test
    void testValidateIfProjectExists_ShouldThrow_WhenProjectNotFound() {
        // Arrange
        when(projectServiceClient.getProject(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(DataValidationException.class, () ->
                postValidator.validateIfProjectExists(1L));
    }
}
