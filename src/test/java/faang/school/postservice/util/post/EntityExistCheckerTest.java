package faang.school.postservice.util.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EntityExistCheckerTest {

    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private EntityExistChecker entityExistChecker;

    @Test
    public void testCurrentUserNotFound() {
        final long currentUserId = 1L;

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(userServiceClient.getUserById(currentUserId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> entityExistChecker.checkCurrentUserExist());

        verify(userContext, times(1)).getUserId();
        verify(userServiceClient, times(1)).getUserById(currentUserId);
    }

    @Test
    public void testCurrentUserFound() {
        final long currentUserId = 1L;
        final UserDto userDto = new UserDto(currentUserId, null, null);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(userServiceClient.getUserById(currentUserId)).thenReturn(userDto);

        long id = entityExistChecker.checkCurrentUserExist();

        assertEquals(currentUserId, id);

        verify(userContext, times(1)).getUserId();
        verify(userServiceClient, times(1)).getUserById(currentUserId);
    }

    @Test
    public void testProjectNotFound() {
        final long projectId = 1L;

        when(projectServiceClient.getProjectById(projectId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> entityExistChecker.checkProjectExist(projectId));

        verify(projectServiceClient, times(1)).getProjectById(projectId);
    }

    @Test
    public void testProjectFound() {
        final long projectId = 1L;
        final long ownerId = 3L;
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);

        when(projectServiceClient.getProjectById(projectId)).thenReturn(projectDto);

        long id = entityExistChecker.checkProjectExist(projectId);

        assertEquals(ownerId, id);

        verify(projectServiceClient, times(1)).getProjectById(projectId);
    }

    @Test
    public void testPostNotFound() {
        final long postId = 5L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> entityExistChecker.checkPostExist(postId));

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void testPostFound() {
        final long postId = 5L;
        final Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post foundedPost = entityExistChecker.checkPostExist(postId);

        assertNotNull(foundedPost);
        assertEquals(post, foundedPost);

        verify(postRepository, times(1)).findById(postId);
    }
}
