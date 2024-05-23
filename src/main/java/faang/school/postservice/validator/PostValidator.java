package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostValidator {
    public void validatePostContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("Post content cannot be empty.");
        }
    }

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            throw new DataValidationException("Author ID or project ID is null.");
        }
    }

    public void validatePublicationPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("The post has already been published");
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            throw new DataValidationException("ID is null");
        }
    }
}
