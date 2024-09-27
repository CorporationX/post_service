package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;

public interface LikeService {

    LikeDto likePost(LikeDto likeDto);
    void unlikePost(LikeDto likeDto);
    LikeDto likeComment(LikeDto likeDto);
    void unlikeComment(LikeDto likeDto);
}
