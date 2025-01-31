package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectExistValidator {
    private final ProjectServiceClient projectServiceClient;

    private static final String PROJECT_NOT_FOUND_ERR_MSG = "Проект с id:%s не найден!";

    public void projectExist(long projectId) {
        ProjectDto projectResponse = projectServiceClient.getProject(projectId);
        log.debug("projectResponse response: {}", projectResponse);
        if (projectResponse == null) {
            throw new DataNotFoundException(String.format(PROJECT_NOT_FOUND_ERR_MSG, projectId));
        }
    }
}
