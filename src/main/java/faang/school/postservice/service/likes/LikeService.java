package faang.school.postservice.service.likes;

public interface LikeService {
    void createPostLike(Long postId);

    void deletePostLike(Long postId);

    void createCommentLike(Long commentId);

    void deleteCommentLike(Long commentId);
}
