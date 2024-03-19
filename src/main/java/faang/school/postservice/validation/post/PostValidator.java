package faang.school.postservice.validation.post;

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
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePostAuthor(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId == null && projectId == null) {
            throw new DataValidationException("Post must have user or project as author");
        }
        if (authorId != null && projectId != null) {
            throw new DataValidationException("Post can't have user and project as authors at the same time");
        }
    }

    public void validateIfAuthorExists(PostDto postDto) {
        Long authorId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (authorId != null) {
            try {
                userServiceClient.getUser(authorId);
            } catch (FeignException exception) {
                throw new EntityNotFoundException("User hasn't been found by id: " + authorId);
            }
        }
        if (projectId != null) {
            try {
                projectServiceClient.findProjectById(projectId);
            } catch (FeignException exception) {
                throw new EntityNotFoundException("Project hasn't been found by id: " + projectId);
            }
        }
    }

    public void validateIfPostIsPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post has already been published");
        }
    }

    public void validateUpdatedPost(Post post, PostDto updatedPostDto) {
        validatePostAuthor(updatedPostDto);

        Long authorId = post.getAuthorId() != null ? post.getAuthorId() : post.getProjectId();
        Long updatedPostAuthorId = updatedPostDto.getAuthorId() != null ?
                updatedPostDto.getAuthorId() : updatedPostDto.getProjectId();

        if (!authorId.equals(updatedPostAuthorId) ||
           (post.getAuthorId() == null && updatedPostDto.getAuthorId() != null) ||
           (post.getProjectId() == null && updatedPostDto.getProjectId() != null)) {
            throw new DataValidationException("Post author can't be changed");
        }
    }
}
