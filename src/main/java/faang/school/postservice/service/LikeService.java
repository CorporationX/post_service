package faang.school.postservice.service;

import faang.school.postservice.dto.CommentLikeDto;
import faang.school.postservice.dto.PostLikeDto;

public interface LikeService {

    PostLikeDto likePost(Long userId, Long postId);

    CommentLikeDto likeComment(Long userId, Long commentId);

    void unlikePost(Long userId, Long postId);

    void unlikeComment(Long userId, Long commentId);

}

