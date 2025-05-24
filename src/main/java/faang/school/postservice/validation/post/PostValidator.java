package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.project.ProjectClientResponseDto;
import faang.school.postservice.dto.user.UserClientResponseDto;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostNotValidException;
import faang.school.postservice.exception.project_service_client.ProjectNotFoundException;
import faang.school.postservice.exception.user_service_client.UserNotFoundException;
import faang.school.postservice.model.post.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void checkPost(Post post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId != null && projectId == null) {
            checkPostAuthor(authorId);
        } else if (authorId == null && projectId != null) {
            checkPostProject(projectId);
        } else {
            log.error("Only one of authorId or projectId must be specified");
            throw new PostNotValidException();
        }
    }

    private void checkPostAuthor(long userId) {
        try {
            UserClientResponseDto userClientResponseDto = userServiceClient.getUser(userId);
            if (userClientResponseDto == null) {
                throw new UserNotFoundException(userId);
            }
        } catch (FeignException.NotFound ex) {
            log.error("User with id {} not found in user-service", userId);
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    private void checkPostProject(long projectId) {
        try {
            ProjectClientResponseDto userClientResponseDto = projectServiceClient.getProject(projectId);
            if (userClientResponseDto == null) {
                throw new ProjectNotFoundException(projectId);
            }
        } catch (FeignException.NotFound ex) {
            log.error("Project with id {} not found in project-service", projectId);
            throw new ProjectNotFoundException(ex.getMessage());
        }
    }

    public void checkPostIsNotPublished(Post post) {
        if (post.isPublished()) {
            log.error("Post with id {} already published", post.getId());
            throw new PostAlreadyPublishedException(post.getId());
        }
    }
}
