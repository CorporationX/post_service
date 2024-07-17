package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {
    LikeDto likeComment(LikeDto likeDto);
    void deleteLikeFromComment(long commentId, long userId);
    LikeDto likePost(LikeDto likeDto);
    void deleteLikeFromPost(long postId, long userId);
}
