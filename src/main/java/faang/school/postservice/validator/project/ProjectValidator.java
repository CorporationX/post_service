package faang.school.postservice.validator.project;

import faang.school.postservice.client.ProjectServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectServiceClient projectServiceClient;

    public boolean isProjectExists(Long projectId) {
        return projectServiceClient.getProject(projectId) != null;
    }
}
