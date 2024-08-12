package faang.school.postservice.validator.project;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectServiceClient projectServiceClient;

    public void checkProjectExist(Long projectId) {
        if (!projectServiceClient.projectExist(projectId)) {
            throw new DataValidationException(String.format("Project with %s doesn't exist", projectId));
        }
    }
}
