package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;

public interface LikeService {
    LikeDto putLikeToPost(long postId);
    LikeDto putLikeToComment(long commentId);
    void deleteLike(long likeId);
    long countLikesFor(PostDto postDto);
}
