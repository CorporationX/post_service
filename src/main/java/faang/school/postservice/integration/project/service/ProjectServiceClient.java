package faang.school.postservice.integration.project.service;

import faang.school.postservice.integration.project.dto.ProjectResponseDto;

import java.net.URISyntaxException;

public interface ProjectServiceClient {
    ProjectResponseDto getProject(long id);
}
