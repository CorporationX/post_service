package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.ProjectContext;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostValidator {

    private final UserContext userContext;
    private final ProjectContext projectContext;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new DataValidationException("The post does not have an author specified");
        }
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new DataValidationException("A post cannot have two authors");
        }
        if (postDto.getAuthorId() != null && userServiceClient.getUser(postDto.getAuthorId()) == null) {
            throw new DataValidationException("The author must be an existing user in the system");
        }
        if (postDto.getProjectId() != null && projectServiceClient.getProject(postDto.getProjectId()) == null) {
            throw new DataValidationException("The author must be an existing project in the system");
        }
    }

    public void isPublishedPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("The post cannot publish that has already been published before");
        }
    }

    public void isDeletedPost(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("The post cannot delete that has already been deleted before");
        }
    }

    public void checkPostAuthorship(Post post) {
        if ((post.getAuthorId() != null && post.getAuthorId() != getContextUserId())
                || (post.getProjectId() != null && post.getProjectId() != getContextProjectId())) {
            throw new DataValidationException("You are not the author of this post or the project does not match");
        }
    }

    public void validateUserExist(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This user is not found");
        }
    }

    public void validateProjectExist(long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This project is not found");
        }
    }

    private long getContextUserId() {
        return userContext.getUserId();
    }

    private long getContextProjectId() {
        return projectContext.getProjectId();
    }

}