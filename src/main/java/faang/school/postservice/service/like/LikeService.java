package faang.school.postservice.service.like;


import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto likePost(Long postId);

    LikeDto removeLikeOnPost(Long postId);

    LikeDto likeComment(Long commentId);

    LikeDto removeLikeOnComment(Long commentId);
}
