package faang.school.postservice.util.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityExistChecker {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

    public long checkCurrentUserExist() {
        long currentUserId = userContext.getUserId();
        if (userServiceClient.getUserById(currentUserId) == null) {
            log.error("Action from unknown user is detected");
            throw new EntityNotFoundException("User doesn't exist");
        }
        return currentUserId;
    }

    public long checkProjectExist(long projectId) {
        ProjectDto currentProject = projectServiceClient.getProjectById(projectId);
        if (currentProject == null) {
            log.error("Project #{} doesn't exist", projectId);
            throw new EntityNotFoundException("Project doesn't exist");
        }
        return currentProject.ownerId();
    }

    public Post checkPostExist(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post #{} doesn't exist", postId);
            return new EntityNotFoundException("Post doesn't exist");
        });
    }
}
