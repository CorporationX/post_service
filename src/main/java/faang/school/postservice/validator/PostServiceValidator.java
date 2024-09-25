package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.post.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostServiceValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validateCreatePost(PostDto postDto) {
        if ((postDto.getAuthorId() == null) && (postDto.getProjectId() == null) ||
                ((postDto.getAuthorId() != null) && (postDto.getProjectId() != null))) {
            throw new DataValidationException("For a post there must be either an author ID or a project ID");
        }

        if (postDto.getAuthorId() != null) {
            validateAuthorId(postDto.getAuthorId());
        } else {
            validateProjectId(postDto.getProjectId());
        }
    }

    public void validateUpdatePost(Post post, PostDto postDto) {
        if (post.getAuthorId() != null &&
                !post.getAuthorId().equals(postDto.getAuthorId())) {
            throw new DataValidationException("The Author Id should not be different");
        }

        if (post.getProjectId() != null &&
                !post.getProjectId().equals(postDto.getProjectId())) {
            throw new DataValidationException("The Project Id should not be different");
        }
    }

    public void validatePublishPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post has already been published");
        }
    }

    public void validateDeletePost(Post postFromTheDatabase) {
        if (postFromTheDatabase.isDeleted()) {
            throw new DataValidationException("Post has already been deleted");
        }
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private void validateProjectId(Long projectId) {
        projectServiceClient.getProject(projectId);
        log.info("Project ID " + projectId + " validated successfully: " + projectId);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private void validateAuthorId(Long authorId) {
        userServiceClient.getUser(authorId);
        log.info("Author ID " + authorId + " validated successfully: ");
    }

    @Recover
    public void recover(FeignException e, Long id) {
        throw new EntityNotFoundException("Entity with ID " + id + " not found");
    }
}
