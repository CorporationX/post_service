package faang.school.postservice;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.utils.PostUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostUtilTest {
    @InjectMocks
    private PostUtil postUtil;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserServiceClient userServiceClient;

    @Test
    void validateCreator_withValidUserId_shouldReturnZero() {
        Long userId = 1L;
        Long projectId = null;
        UserDto userDto = new UserDto(
                1L, "iam", "iam23@mail.ru"
        );
        when(userServiceClient.getUser(userId)).thenReturn(userDto);

        int result = postUtil.validateCreator(userId, projectId);

        assertEquals(0, result);
        verify(userServiceClient, times(1)).getUser(userId);
        verifyNoInteractions(projectServiceClient);
    }

    @Test
    void validateCreator_withValidProjectId_shouldReturnOne() {
        Long userId = null;
        Long projectId = 2L;
        ProjectDto projectDto = new ProjectDto(
                1L, "something"
        );
        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);

        int result = postUtil.validateCreator(userId, projectId);

        assertEquals(1, result);
        verify(projectServiceClient, times(1)).getProject(projectId);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void validateCreator_withInvalidIds_shouldThrowException() {
        Long userId = 1L;
        Long projectId = 2L;

        when(userServiceClient.getUser(userId)).thenReturn(null);
        when(projectServiceClient.getProject(projectId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> postUtil.validateCreator(userId, projectId));

        verify(userServiceClient, times(1)).getUser(userId);
        verify(projectServiceClient, times(1)).getProject(projectId);
    }

    @Test
    void validateCreator_withBothIdsNull_shouldThrowException() {
        Long userId = null;
        Long projectId = null;

        assertThrows(IllegalArgumentException.class, () -> postUtil.validateCreator(userId, projectId));

        verifyNoInteractions(userServiceClient, projectServiceClient);
    }
}
