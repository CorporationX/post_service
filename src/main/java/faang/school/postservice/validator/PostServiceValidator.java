package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

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

    public void validateUpdatePost(PostDto postDto) {
        Post postFromTheDatabase = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (postFromTheDatabase.getAuthorId() != null &&
                !postFromTheDatabase.getAuthorId().equals(postDto.getAuthorId())) {
            throw new DataValidationException("The Author Id should not be different");
        }

        if (postFromTheDatabase.getProjectId() != null &&
                !postFromTheDatabase.getProjectId().equals(postDto.getProjectId())) {
            throw new DataValidationException("The Project Id should not be different");
        }

        if (postFromTheDatabase.isDeleted() != postDto.isDeleted()) {
            throw new DataValidationException("The Post flag deleted should not be different");
        }

        if (postFromTheDatabase.isPublished() != postDto.isPublished()) {
            throw new DataValidationException("The Post flag published should not be different");
        }
    }

    public void validatePublishPost(PostDto postDto) {
        Post postFromTheDatabase = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (postFromTheDatabase.isPublished()) {
            throw new DataValidationException("The Post flag published must be false");
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
