package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    private static final int POST_LENGTH_MAX = 4096;

    public void validatePostCreator(PostDto post, ProjectDto project, UserDto user) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();

        if (authorId != null && projectId != null) {
            throw new DataValidationException("Author and project cannot be specified at the same time");
        }

        if (authorId != null) {
            validateAuthor(user);
        } else if (projectId != null) {
            validateProject(project);
        }
    }

    public void validationOfPostCreation(PostDto post) {

        if (post.getContent().length() > POST_LENGTH_MAX) {
            throw new DataValidationException("Content is too long");
        }

        if (post.getContent().isBlank() || post.getContent().isEmpty()) {
            throw new DataValidationException("Content is empty");
        }
    }

    public void validatePublishPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post is already published");
        }
    }

    public void validateAuthor(UserDto user) {
        if (user == null) {
            throw new DataValidationException("User not found");
        }
    }

    public void validateProject(ProjectDto project) {
        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        }
    }
}
