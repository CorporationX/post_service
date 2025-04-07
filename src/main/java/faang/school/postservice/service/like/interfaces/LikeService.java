package faang.school.postservice.service.like.interfaces;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {

    LikeDto likePost(long postId);

    void unlikePost(long postId);

    LikeDto likeComment(long commentId);

    void unlikeComment(long commentId);

}
