package faang.school.postservice.validator.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

public interface LikeValidator {

    Post validateLikeOnPost(long postId, long userId, boolean doSet);
    Comment validateLikeOnComment(long commentId, long userId, boolean doSet);
}
