package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostValidator {
    public void validatePostContent(String content) {
        if (content == null || content.isBlank()) {
            log.error("Post content cannot be empty.");
            throw new DataValidationException("Post content cannot be empty.");
        }
    }

    public void validateAuthorIdAndProjectId(Long authorId, Long projectId) {
        if (authorId == null && projectId == null) {
            log.error("Author ID or project ID is null.");
            throw new DataValidationException("Author ID or project ID is null.");
        }
    }

    public void validatePublicationPost(Post post) {
        if (post.isPublished()) {
            log.error("The post has already been published.");
            throw new DataValidationException("The post has already been published.");
        }
    }

    public void validateId(Long id) {
        if (id == null) {
            log.error("ID is null.");
            throw new DataValidationException("ID is null.");
        }
    }
}
