package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.handler.FeignExceptionHandler;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectFeignService {

    private final ProjectServiceClient projectServiceClient;
    private final FeignExceptionHandler feignExceptionHandler;

    public ProjectDto getProjectOrFail(@NonNull Long projectId) {
        try {
           return projectServiceClient.getProject(projectId);
        } catch (FeignException feignException) {
            throw feignExceptionHandler.handleFeignException(
                    feignException,
                    "Project",
                    projectId
            );
        }
    }
}
