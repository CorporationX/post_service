package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public void createDraftPostValidator(PostDto postDto) {
        boolean userExists = postDto.authorId() != null;
        boolean projectExists = postDto.projectId() != null;

        validateSingleCreator(userExists, projectExists);

        if (userExists) {
            validateUserExists(postDto.authorId());
        } else {
            validateProjectExists(postDto.projectId());
        }
    }

    public void publishPostValidator(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post is already published");
        }
    }

    public void updatePostValidator(Post post, PostDto postDto) {
        boolean isUserChanged = !Objects.equals(post.getAuthorId(), postDto.authorId());
        boolean isProjectChanged = !Objects.equals(post.getProjectId(), postDto.projectId());

        if (post.getAuthorId() != null && isUserChanged) {
            throw new DataValidationException("Post author cannot be changed");
        }

        if (post.getProjectId() != null && isProjectChanged) {
            throw new DataValidationException("Post project cannot be changed");
        }
    }

    public void validateIfAuthorExists(Long authorId) {
        Optional.ofNullable(userServiceClient.getUser(authorId))
                .orElseThrow(() -> new DataValidationException("Author " + authorId + " not found"));
    }

    public void validateIfProjectExists(Long projectId) {
        Optional.ofNullable(projectServiceClient.getProject(projectId))
                .orElseThrow(() -> new DataValidationException("Project " + projectId + " not found"));
    }

    private void validateSingleCreator(boolean userExists, boolean projectExists) {
         if (userExists && projectExists) {
            throw new DataValidationException("Post can not be created by user and project at the same time");
        }
        if (!userExists && !projectExists) {
            throw new DataValidationException("Post must have an author (user or project)");
        }
    }

    private void validateUserExists(Long userId) {
        Optional.ofNullable(userServiceClient.getUser(userId))
                .orElseThrow(() -> new DataValidationException("User " + userId + " not found"));
    }

    private void validateProjectExists(Long projectId) {
        Optional.ofNullable(projectServiceClient.getProject(projectId))
                .orElseThrow(() -> new DataValidationException("Project " + projectId + " not found"));
    }
}
