package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.PublishPostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    public void validateToAdd(PostDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null ||
                dto.getAuthorId() == null && dto.getProjectId() == null) {
            throw new CreatePostException("There is should be only one author");
        }
    }

    public void validateToPublish(Post postById) {
        if (postById.isPublished()) {
            throw new PublishPostException("Post is already published");
        }

        if (postById.isDeleted()) {
            throw new PublishPostException("Post is already deleted");
        }
    }
}
