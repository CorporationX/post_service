package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LikeService {
    void addLikeToPost(LikeDto likeDto, long postId);

    void deleteLikeFromPost(LikeDto likeDto, long postId);

    void addLikeToComment(LikeDto likeDto, long commentId);

    void deleteLikeFromComment(LikeDto likeDto, long commentId);

    List<Like> findLikesOfPublishedPost(long postId);
}
