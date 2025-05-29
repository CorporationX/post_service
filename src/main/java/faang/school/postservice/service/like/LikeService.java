package faang.school.postservice.service.like;

import faang.school.postservice.model.Like;

public interface LikeService {
    Like likeThePost(Like like, long postId);
    void deleteLikeThePost(long postId);
    Like likeTheComment(Like like, long commentId);
    void deleteLikeTheComment(long commentId);
    long countTheLikeForPost(long postId);
}
