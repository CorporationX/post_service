package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
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

    public void validatePublishPost(Post post, PostDto postDto) {
        if (post.isPublished()) {
            throw new DataValidationException("Post has already been published");
        }
    }

    public void validateDeletePost(Post postFromTheDatabase) {
        if (postFromTheDatabase.isDeleted()) {
            throw new DataValidationException("Post has already been deleted");
        }
    }

    private void validateProjectId(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException c) {
            throw new EntityNotFoundException("Project id " + projectId + " not found");
        }
    }

    private void validateAuthorId(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException c) {
            throw new EntityNotFoundException("Author id " + authorId + " not found");
        }
    }
}
