package faang.school.postservice.service.like;

public interface LikeService {
    void likePost(long userId, long postId);

    void likeComment(long userId, long commentId);

    void deleteLikeFromPost(long userId, long postId);

    void deleteLikeFromComment(long userId, long commentId);
}
