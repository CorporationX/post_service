package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClientMock;
import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostValidator {
    private final UserServiceClientMock userServiceClient;
    private final ProjectServiceClientMock projectServiceClient;

    public void validateCreatePost(Post post) {
        checkInputAuthorOrProject(post);

        if (post.getAuthorId() != null) {
            checkUserExists(post.getAuthorId());
        }

        if (post.getProjectId() != null) {
            checkProjectExists(post.getProjectId());
        }
    }

    public void checkInputAuthorOrProject(Post post) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId == null && projectId == null) {
            throw new ValidationException("AuthorId or projectId must be filled in");
        }

        if (authorId != null && projectId != null) {
            throw new ValidationException("Specify either authorId or projectId to create a post");
        }
    }

    private void checkUserExists(Long userId) {
        try {
            if (userServiceClient.getUser(userId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
        }

        throw new ValidationException("User id=%s not exist", userId);
    }

    private void checkProjectExists(Long projectId) {
        try {
            if (projectServiceClient.getProject(projectId) != null) {
                return;
            }
        } catch (RuntimeException ignore) {
        }

        throw new ValidationException("Project id=%s not exist", projectId);
    }
}
