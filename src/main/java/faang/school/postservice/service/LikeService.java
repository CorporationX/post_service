package faang.school.postservice.service;

public interface LikeService {

    void addLikePost(Long postId, Long userId);

    void removeLikePost(Long postId, Long userId);

    void addLikeComment(Long commentId, Long userId);

    void removeLikeComment(Long commentId, Long userId);
}
