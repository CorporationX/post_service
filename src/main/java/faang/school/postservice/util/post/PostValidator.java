package faang.school.postservice.util.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class PostValidator {

    public void validateUser(long currentUserId, PostDto postDto) {
        if (postDto.authorId() != currentUserId) {
            log.error("Request includes two different User's IDs: #{} and #{}", currentUserId, postDto.authorId());
            throw new DataValidationException("User definition error is occur");
        }
    }

    public void validateProject(long ownerId, long currentUserId, long projectId) {
        if (ownerId != currentUserId) {
            log.error("User #{} is not owner of the Project #{}", currentUserId, projectId);
            throw new DataValidationException("User must be project owner");
        }
    }

    public void validateIds(PostDto postDto) {
        if (postDto.authorId() == null && postDto.projectId() == null) {
            log.error("Unknown author is trying to act the post");
            throw new DataValidationException("Author or Project must exist");
        }
        if (postDto.authorId() != null && postDto.projectId() != null) {
            log.error("Post is being acted by Author #{} and Project #{} at the same time",
                    postDto.authorId(), postDto.projectId());
            throw new DataValidationException("Unable to act Post by Author and Project at the same time");
        }
    }

    public void validatePostIsUnpublished(Post post) {
        if (!post.isPublished()) {
            log.error("Post #{} has not been published yet", post.getId());
            throw new DataValidationException("This post has not been published yet");
        }
    }

    public void validatePostIsPublished(Post post) {
        if (post.isPublished()) {
            log.error("Post #{} has already been published", post.getId());
            throw new DataValidationException("This post has already been published");
        }
    }

    public void validatePostIsDeleted(Post post) {
        if (post.isDeleted()) {
            log.error("Post #{} has been deleted", post.getId());
            throw new DataValidationException("This post has been deleted");
        }
    }

    public void validateChangeAuthor(Post currentPost, PostDto postDto) {
        if (!Objects.equals(currentPost.getAuthorId(), postDto.authorId())
                || !Objects.equals(currentPost.getProjectId(), postDto.projectId())) {
            log.error("Attempt to change author of the post #{}", currentPost.getId());
            throw new DataValidationException("Unable to change author of the post");
        }
    }
}
