package faang.school.postservice.validator.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.entity.Post;
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


    // Validates if post has only 1 author (ex. by user or by project but not both or no one)
    public void createDraftPostValidator(PostDto postDto) {
        boolean authorExists = postDto.authorId() != null;
        boolean projectExists = postDto.projectId() != null;

        validateSingleCreator(authorExists, projectExists);

        if (authorExists) {
            validateAuthorExists(postDto.authorId());
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
        boolean isAuthorChanged = !Objects.equals(post.getAuthorId(), postDto.authorId());
        boolean isProjectChanged = !Objects.equals(post.getProjectId(), postDto.projectId());

        if (post.getAuthorId() != null && isAuthorChanged) {
            throw new DataValidationException("Post author cannot be changed");
        }

        if (post.getProjectId() != null && isProjectChanged) {
            throw new DataValidationException("Post project cannot be changed");
        }
    }

    public void getAllDraftsByAuthorIdValidator(Long authorId) {
        Optional.ofNullable(userServiceClient.getUser(authorId))
                .orElseThrow(() -> new DataValidationException("Author " + authorId + " not found"));
    }

    public void getAllDraftsByProjectIdValidator(Long projectId) {
        Optional.ofNullable(projectServiceClient.getProject(projectId))
                .orElseThrow(() -> new DataValidationException("Project " + projectId + " not found"));
    }

    private void validateSingleCreator(boolean authorExists, boolean projectExists) {
        if (authorExists && projectExists) {
            throw new DataValidationException("Post can be created by only one entity, not both");
        }
        if (!authorExists && !projectExists) {
            throw new DataValidationException("Post cannot be created by no one");
        }
    }

    private void validateAuthorExists(Long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new DataValidationException("Author " + authorId + " not found");
        }
    }

    private void validateProjectExists(Long projectId) {
        if (projectServiceClient.getProject(projectId) == null) {
            throw new DataValidationException("Project " + projectId + " not found");
        }
    }
}
