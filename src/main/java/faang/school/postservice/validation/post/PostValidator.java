package faang.school.postservice.validation.post;

import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.model.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostValidator {
    // TODO: проверка что пользователь или проект существуют
    public void validatePost() {

    }

    public void checkPostIsNotPublished(Post post) {
        if (post.isPublished()) {
            log.error("Post with id {} already published", post.getId());
            throw new PostAlreadyPublishedException(post.getId());
        }
    }
}
