package faang.school.postservice.util.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ScheduledTaskDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.util.exception.CreatePostException;
import faang.school.postservice.util.exception.DeletePostException;
import faang.school.postservice.util.exception.GetPostException;
import faang.school.postservice.util.exception.PostNotFoundException;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.exception.UpdatePostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostServiceValidator {

    public void validateToAdd(PostDto dto) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null ||
                dto.getAuthorId() == null && dto.getProjectId() == null) {
            throw new CreatePostException("Post's author can be only author or project and can't be both");
        }
    }

    public void validateToPublish(Post post) {
        if (post.isPublished()) {
            throw new PublishPostException("Post is already published");
        }

        if (post.isDeleted()) {
            throw new PublishPostException("Post is already deleted");
        }
    }

    public void validateToUpdate(Post post, String content) {
        if (post.isDeleted()) {
            throw new UpdatePostException("Post is already deleted");
        }
        if (!post.isPublished()) {
            throw new UpdatePostException("Post is in draft state. It can't be updated");
        }
        if (post.getContent().equals(content)) {
            throw new UpdatePostException("There is no changes to update");
        }
    }

    public void validateToDelete(Post post) {
        if (post.isDeleted()) {
            throw new DeletePostException("Post is already deleted");
        }
        if (!post.isPublished()) {
            throw new DeletePostException("Post is in draft state. It can't be deleted");
        }
    }

    public void validateToGet(Post postById) {
        if (postById.isDeleted()) {
            throw new GetPostException("Post is already deleted");
        }
        if (!postById.isPublished()) {
            throw new GetPostException("Post is in draft state. It can't be gotten");
        }
    }

    public void validateToPublishPostBySchedule(ScheduledTaskDto dto) {
        // TODO: 09.08.2023  
    }

    public void validateToDeletePostBySchedule(Optional<Post> post, Long postId) {
        if (post.isEmpty()) {
            throw new PostNotFoundException(
                    "Post with id = " + String.format("%d", postId) + " not found"
            );
        }
    }
}
