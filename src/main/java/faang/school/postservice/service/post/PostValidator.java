package faang.school.postservice.service.post;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static faang.school.postservice.model.PostStatus.DRAFT;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostValidator {

    public static void validateUserIsPostAuthor(Long userId, Long postAuthorId) {
        if (!Objects.equals(userId, postAuthorId)) {
            throw new ForbiddenException(String.format("This user %d is not the creator of the post %d.",
                    userId, postAuthorId));
        }
    }

    public static Post validatePostExists(Optional<Post> optionalPost, Long postId) {
        if (optionalPost.isEmpty()) {
            throw new DataValidationException(String.format("This post %d does not exist.", postId));
        } else {
            return optionalPost.get();
        }
    }

    public static void validatePostIsNotPublished(Post post) {
        if (post.isPublished() && !Objects.equals(post.getPostStatus(), DRAFT)) {
            throw new ForbiddenException(String.format("The post %d has already been published.", post.getId()));
        }
    }
}
