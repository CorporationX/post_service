package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class PostValidator {

    public void validatePost(PostDto postDto) {
        Long authorId = postDto.getAuthorId(), projectId = postDto.getProjectId();
        log.info("authorId: {}, projectId: {}", authorId, projectId);

        if (postDto.getContent().isBlank() || isNotValidCreator(authorId, projectId)) {
            log.warn("PostDto is not valid");
            throw new DataValidationException("Either an author or a project is required");
        }
    }

    public Post validatePost(Optional<Post> post, long id) {
        log.info("validatePost: {}", post);
        return post.orElseThrow(() ->
                new EntityNotFoundException("There is no post with ID " + id));
    }

    public void validateCreator(boolean exists) {
        log.info("Creator exists: {}", exists);
        if (!exists) {
            throw new DataValidationException("There is no project/user");
        }
    }

    private boolean isNotValidCreator(Long authorId, Long projectId) {
        return (authorId == null && projectId == null) || (authorId != null && projectId != null);
    }
}
