package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public void validatePostAuthor(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            throw new DataValidationException("Post must have user or project as author");
        }
        if (authorId != null && projectId != null) {
            throw new DataValidationException("Post can't have user and project as authors at the same time");
        }
    }

    public void validateIfAuthorExistsById(long authorId) {
        userServiceClient.getUser(authorId);
    }

    public void validateIfProjectExistsById(long projectId) {
        projectServiceClient.findProjectById(projectId);
    }

    public void validateIfPostIsPublished(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post has already been published");
        }
    }

    public void validateUpdatedPost(Post post, PostDto updatedPostDto) {
        if (updatedPostDto.getAuthorId() == null && updatedPostDto.getProjectId() == null) {
            throw new DataValidationException("Post author can't be deleted");
        }

        Long authorId = post.getAuthorId() != null ? post.getAuthorId() : post.getProjectId();
        Long updatedPostAuthorId = updatedPostDto.getAuthorId() != null ?
                updatedPostDto.getAuthorId() : updatedPostDto.getProjectId();

        if (!authorId.equals(updatedPostAuthorId)) {
            throw new DataValidationException("Post author can't be changed");
        }
    }
}
