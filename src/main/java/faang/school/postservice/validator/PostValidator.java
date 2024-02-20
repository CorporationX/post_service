package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostValidator {

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public void validateAccessAndContent(PostDto postDto) {
        validateAccessToPost(postDto.getAuthorId(), postDto.getProjectId());
        validatePostContent(postDto.getContent());
    }

    public void validatePostContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Post content cannot be blank");
        }
    }

    public void validateAccessToPost(Long postAuthorId, Long postProjectId) {
        if (Objects.equals(postAuthorId, postProjectId)) {
            throw new DataValidationException("Post cannot belong to both author and project or be null");
        }

        long userId = userContext.getUserId();

        if (postAuthorId != null) {
            validateOwnershipUserToPost(postAuthorId, userId);
        } else {
            validateOwnershipProjectToPost(postProjectId, userId);
        }
    }

    private void validateOwnershipProjectToPost(Long postProjectId, long userId) {
        if (!projectServiceClient.existProjectById(postProjectId)) {
            throw new EntityNotFoundException(String.format("Project with id %s not found", postProjectId));
        }
        List<Long> projectIdsUserHasAccess = projectServiceClient.getAll()
                .stream().filter(prj -> prj.getOwnerId() == userId)
                .map(ProjectDto::getId)
                .toList();
        if (!projectIdsUserHasAccess.contains(postProjectId)) {
            throw new SecurityException("Project is not author of the post");
        }
    }

    private void validateOwnershipUserToPost(Long postAuthorId, long userId) {
        if (!userServiceClient.existsUserById(postAuthorId)) {
            throw new EntityNotFoundException(String.format("User with id %s not found", postAuthorId));
        }
        if (postAuthorId != userId) {
            throw new SecurityException("You are not the author of the post");
        }
    }
}