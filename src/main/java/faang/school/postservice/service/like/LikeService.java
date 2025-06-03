package faang.school.postservice.service.like;

import faang.school.postservice.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeService {
    Like likeThePost(long postId);
    void deleteLikeThePost(long postId);
    Like likeTheComment(long commentId);
    void deleteLikeTheComment(long commentId);
    List<Like> getAllTheLikeForPost(long postId);
    List<Like> countTheLikeForComment(long comment);
}
