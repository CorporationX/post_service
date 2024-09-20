package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.postservice.post.PostMock.authorId;
import static faang.school.postservice.post.PostMock.content;
import static faang.school.postservice.post.PostMock.generatePostDto;
import static faang.school.postservice.post.PostMock.generateProjectDto;
import static faang.school.postservice.post.PostMock.generateUserDto;
import static faang.school.postservice.post.PostMock.projectId;
import static faang.school.postservice.post.PostMock.userId;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator validator;

    @Test
    @DisplayName("Validation should succeed with author id")
    public void validateWithAuthor() {
        // Arrange
        PostDto postDto = generatePostDto(authorId, null, false, content);

        when(userServiceClient.getUser(userId)).thenReturn(generateUserDto());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(postDto));
    }

    @Test
    @DisplayName("Validation should succeed with project id")
    public void validateWithProject() {
        // Arrange
        PostDto postDto = generatePostDto(null, projectId, false, content);

        when(projectServiceClient.getProject(projectId)).thenReturn(generateProjectDto());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(postDto));
    }

    @Test
    @DisplayName("Validation should fail when author id and project id are both set")
    public void validateShouldThrowWithAuthorAndProject() {
        // Arrange
        PostDto postDto = generatePostDto(authorId, projectId, false, content);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> validator.validate(postDto));
    }

    @Test
    @DisplayName("Validation should fail when author id is not existed")
    public void validateShouldThrowWithNoAuthor() {
        // Arrange
        PostDto postDto = generatePostDto(authorId, null, false, content);

        when(userServiceClient.getUser(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> validator.validate(postDto));
    }

    @Test
    @DisplayName("Validation should fail when project id is not existed")
    public void validateShouldThrowWithNoProject() {
        // Arrange
        PostDto postDto = generatePostDto(null, projectId, false, content);

        when(projectServiceClient.getProject(projectId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> validator.validate(postDto));
    }
}