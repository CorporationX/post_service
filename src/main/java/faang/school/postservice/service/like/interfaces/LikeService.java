package faang.school.postservice.service.like.interfaces;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {

    LikeDto likePost(long postId, long userId);

    void unlikePost(long postId, long userId);

    LikeDto likeComment(long commentId, long userId);

    void unlikeComment(long commentId, long userId);

}
