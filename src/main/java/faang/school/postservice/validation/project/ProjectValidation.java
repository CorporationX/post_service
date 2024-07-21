package faang.school.postservice.validation.project;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidation {

    private final ProjectServiceClient projectServiceClient;

    public void doesProjectExist(Long projectId) {
        if (!projectServiceClient.doesProjectExist(projectId)) {
            throw new DataValidationException(String.format("Project with %s doesn't exist", projectId));
        }
    }
}
