package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectClientResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.client.RemoteNotFoundException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.entity.post.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostValidator postValidator;

    private Post post;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(5L);
    }

    @Test
    public void testCheckPostForCurrentUser_AuthorFound() {
        when(userServiceClient.getCurrentUser()).thenAnswer(invocation -> {
            UserDto userDto = new UserDto();
            userDto.setId(1L);
            return userDto;
        });

        assertDoesNotThrow(() ->  postValidator.checkPostForCurrentUser());
        verify(userServiceClient, times(1)).getCurrentUser();
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    public void testCheckPostForCurrentUser_AuthorNotFound() {
        when(userServiceClient.getCurrentUser()).thenThrow(RemoteNotFoundException.class);

        assertThrows(RemoteNotFoundException.class, () -> postValidator.checkPostForCurrentUser());
        verify(userServiceClient, times(1)).getCurrentUser();
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    public void testCheckPostForProject_ProjectFound() {
        post.setProjectId(2L);

        when(projectServiceClient.getProject(eq(post.getProjectId()))).thenAnswer(invocation -> {
            Long projectId = invocation.getArgument(0);
            ProjectClientResponseDto projectDto = new ProjectClientResponseDto();
            projectDto.setId(projectId);
            return projectDto;
        });

        assertDoesNotThrow(() ->  postValidator.checkPostForProject(post.getProjectId()));
        verify(userServiceClient, never()).getCurrentUser();
        verify(projectServiceClient, times(1)).getProject(eq(post.getProjectId()));
    }

    @Test
    public void testCheckPostForProject_ProjectNotFound() {
        post.setProjectId(2L);

        when(projectServiceClient.getProject(eq(post.getProjectId()))).thenThrow(RemoteNotFoundException.class);

        assertThrows(RemoteNotFoundException.class, () -> postValidator.checkPostForProject(post.getProjectId()));
        verify(userServiceClient, never()).getCurrentUser();
        verify(projectServiceClient, times(1)).getProject(eq(post.getProjectId()));
    }

    @Test
    public void checkPostIsNotPublished_postNotPublished() {
        assertDoesNotThrow(() -> postValidator.checkPostIsNotPublished(post));
    }

    @Test
    public void checkPostIsNotPublished_postAlreadyPublished() {
        post.setPublished(true);

        assertThrows(PostAlreadyPublishedException.class, () -> postValidator.checkPostIsNotPublished(post));
    }
}
