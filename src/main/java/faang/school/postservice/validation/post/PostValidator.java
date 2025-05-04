package faang.school.postservice.validation.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostValidator {

    public boolean shouldSkip(Post post, Long viewerId) {
        return post.isDeleted()
                || post.getAuthorId() == null
                || post.getAuthorId().equals(viewerId);
    }
}
