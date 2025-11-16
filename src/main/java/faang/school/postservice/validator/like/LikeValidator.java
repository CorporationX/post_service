package faang.school.postservice.validator.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public interface LikeValidator {

    public Post validateLikeOnPost(long postId, long userId, boolean doSet);

    public Comment validateLikeOnComment(long commentId, long userId, boolean doSet);
}
