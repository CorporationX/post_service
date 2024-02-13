package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostValidator {

    /*private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public void fullValidation(PostDto postDto) {
        contentValidation(postDto);
        accessAndOwnerExistenceValidation(postDto);
    }

    public void contentValidation (PostDto postDto) {
        String content = postDto.getContent();

        if (content.isBlank()) {
            throw new ValidationException("Content of post must not be blank or null");
        }
    }

    public void accessAndOwnerExistenceValidation(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        long authorizedUser = userContext.getUserId();

        boolean isAuthorOwner = authorId != null;
        long ownerId = authorId != null ? authorId : projectId;

        if (Objects.equals(authorId, projectId)) {
            throw new IllegalArgumentException(
                    String.format("author id = %s and project id = %s cannot be the same or both null", authorId, projectId));
        }

        if (isAuthorOwner) {
            if (authorizedUser != ownerId) {
                throw new SecurityException(String.format("user with id = %s has not access to this post", authorizedUser));
            }
            userServiceClient.getUser(ownerId);//throws
        }
        if (!isAuthorOwner) {
            ProjectDto project = projectServiceClient.getProject(ownerId);//throws
            long projectOwnerId = project.getOwnerId();
            if (authorizedUser != projectOwnerId) {
                throw new SecurityException(String.format("user with id = %s has not access to this post", authorizedUser));
            }
        }
    }*/
}
