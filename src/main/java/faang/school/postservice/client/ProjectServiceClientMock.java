package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectServiceClientMock implements ProjectServiceClient {
    public ProjectDto getProject(long projectId) {
        return new ProjectDto();
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return new ArrayList<>();
    }
}
