package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;

public interface LikeService {
    void putLikeToPost(long postId);
    void putLikeToComment(long commentId);
    void deleteLike(long likeId);
    long countLikesFor(PostDto postDto);
}
