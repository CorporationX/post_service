package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {

    public void validateCreate(PostDto post, UserDto user, ProjectDto project) {
        if (user == null || project == null) {
            throw new DataValidationException("User or project not found");
        }

        if (post.getContent().length() > Post.POST_LENGTH_MAX) {
            throw new DataValidationException("Content is too long");
        }
        if (post.getContent().isBlank() || post.getContent().isEmpty()) {
            throw new DataValidationException("Content is empty");
        }
    }
}
