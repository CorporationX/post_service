package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;


public interface LikeService {
    LikeDto likePost(LikeDto likeDto);

    LikeDto likeComment(LikeDto likeDto);

    void deleteLikePost(long postId);

    void deleteLikeComment(long commentId);

}
