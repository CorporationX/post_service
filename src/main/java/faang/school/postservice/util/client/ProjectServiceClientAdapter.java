package faang.school.postservice.util.client;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectServiceClientAdapter {

    private final ProjectServiceClient projectServiceClient;

    public ProjectDto getProjectById(long projectId) {
        try {
            return projectServiceClient.getProjectById(projectId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
