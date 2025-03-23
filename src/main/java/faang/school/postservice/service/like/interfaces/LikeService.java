package faang.school.postservice.service.like.interfaces;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {

    LikeDto likePost(long postId, LikeDto likeDto);

    void unlikePost(long postId, LikeDto likeDto);

    LikeDto likeComment(long commentId, LikeDto likeDto);

    void unlikeComment(long commentId, LikeDto likeDto);

}
