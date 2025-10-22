package faang.school.postservice.service.post;

import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Post;

import java.util.Objects;

public class PostValidator {

    public static void validateUserIsPostAuthor(Long userId, Long postAuthorId) {
        if (!Objects.equals(userId, postAuthorId)) {
            throw new ForbiddenException(String.format("This user %d is not the creator of the post %d.",
                    userId, postAuthorId));
        }
    }

    public static void validatePostIsNotPublished(Post post) {
        if (post.isPublished()) {
            throw new ForbiddenException(String.format("The post %d has already been published.", post.getId()));
        }
    }

    public static void ensurePostIsNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new ForbiddenException(String.format("The post %d has already been deleted", post.getId()));
        }
    }
}
