package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto setLikeOnPost(long postId);
    void unsetLikeOnPost(long postId);
    LikeDto setLikeOnComment(long commentId);
    void unsetLikeOnComment(long commentId);
    int getPostLikesCount(long postId);
}
