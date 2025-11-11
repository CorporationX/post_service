package faang.school.postservice.util.client;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceClientAdapterTest {

    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private ProjectServiceClientAdapter projectServiceClientAdapter;

    @Test
    public void testProjectNotFound() {
        final long projectId = 1L;

        when(projectServiceClient.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException("Project not found"));

        assertThrows(EntityNotFoundException.class, () -> projectServiceClientAdapter.getProjectById(projectId));

        verify(projectServiceClient, times(1)).getProjectById(projectId);
    }

    @Test
    public void testProjectFound() {
        final long projectId = 1L;
        final long ownerId = 3L;
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);

        when(projectServiceClient.getProjectById(projectId)).thenReturn(projectDto);

        long id = projectServiceClientAdapter.getProjectById(projectId).ownerId();

        assertEquals(ownerId, id);

        verify(projectServiceClient, times(1)).getProjectById(projectId);
    }
}
