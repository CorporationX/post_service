package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePost(PostDto postDto) {
        Long authorId = postDto.getAuthorId(), projectId = postDto.getProjectId();
        log.info("authorId: {}, projectId: {}", authorId, projectId);

        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            log.warn("PostDto is not valid");
            throw new DataValidationException("Either an author or a project is required");
        }
    }

    public void createDraftPostValidator(PostDto postDto) {
        boolean userExists = postDto.getAuthorId() != null;
        boolean projectExists = postDto.getProjectId() != null;

        validateSingleCreator(userExists, projectExists);

        if (userExists) {
            validateUserExists(postDto.getAuthorId());
        } else {
            validateProjectExists(postDto.getProjectId());
        }
    }

    public void publishPostValidator(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post is already published");
        }
    }

    public void updatePostValidator(Post post, PostDto postDto) {
        boolean isUserChanged = !Objects.equals(post.getAuthorId(), postDto.getAuthorId());
        boolean isProjectChanged = !Objects.equals(post.getProjectId(), postDto.getProjectId());

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
