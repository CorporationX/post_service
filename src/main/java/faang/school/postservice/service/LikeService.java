package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;

import java.util.List;

public interface LikeService {
    void addLikeToPost(LikeDto likeDto, long postId);

    void deleteLikeFromPost(LikeDto likeDto, long postId);

    void addLikeToComment(LikeDto likeDto, long commentId);

    void deleteLikeFromComment(LikeDto likeDto, long commentId);

    List<LikeDto> findLikesOfPublishedPost(long postId);
}
