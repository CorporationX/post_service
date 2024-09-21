package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostValidator {

    public void validatePost(PostDto postDto) {
        Long authorId = postDto.getAuthorId(), projectId = postDto.getProjectId();
        log.info("authorId: {}, projectId: {}", authorId, projectId);

        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            log.warn("PostDto is not valid");
            throw new DataValidationException("Either an author or a project is required");
        }
    }
}
