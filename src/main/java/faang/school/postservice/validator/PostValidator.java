package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostValidator {

    public void validatePost(PostDto postDto) {
        Long authorId = postDto.getAuthorId(), projectId = postDto.getProjectId();
        log.info("authorId: {}, projectId: {}", authorId, projectId);

        if (postDto.getContent().isBlank() || isNotValidCreator(authorId, projectId)) {
            throw new DataValidationException("Either an author or a project is required");
        }
    }

    public PostDto validatePostWithReturnDto(PostDto postDto) {
        log.info("validatePost: {}", postDto);

        if (postDto == null) {
            throw new EntityNotFoundException("There is no such post");
        }

        return postDto;
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
