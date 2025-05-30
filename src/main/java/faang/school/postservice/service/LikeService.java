package faang.school.postservice.service;

import java.util.List;

import faang.school.postservice.dto.LikeDto;

public interface LikeService {
    LikeDto putLikeToPost(long postId);
    LikeDto putLikeToComment(long commentId);
    void deleteLike(long likeId);
    void deleteLikeForPost(long postId);
    void deleteLikeForComment(long commentId);
    int countLikesFor(Long postId);
    List<LikeDto> getLikesByUser(); 
}
