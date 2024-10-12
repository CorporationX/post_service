package faang.school.postservice.service;

import faang.school.postservice.model.dto.like.LikeDto;

public interface LikeService {

    LikeDto createLikeComment(Long commentId);

    void deleteLikeComment(Long commentId);

    LikeDto createLikePost(Long postId);

    void deleteLikePost(Long commentId);
}
