package faang.school.postservice.validator.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

public interface LikeValidator {

    Post validateAndGetPostToLike(long userId, long postId);

    Comment validateAndGetCommentToLike(long userId, long commentId);

    void validateUserExistence(long userId);
}
