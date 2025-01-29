package faang.school.postservice.utils;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostUtil {
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;

    public int validateCreator(Long userId, Long projectId) {
        if (userId == null && projectId == null) {
            throw new IllegalArgumentException("There must be one id : user or project!");
        }
        if (userId != null && getUser(userId) != null) {
            return 0;
        }
        if (projectId != null && getProject(projectId) != null) {
            return 1;
        }
        throw new IllegalArgumentException("User or project was not found!");
    }

    public ProjectDto getProject(Long id) {
        return projectServiceClient.getProject(id);
    }

    public UserDto getUser(Long id) {
        return userServiceClient.getUser(id);
    }
}
