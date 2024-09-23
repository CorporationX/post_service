package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface LikeService {
    void addLikeToPost(@Valid LikeDto likeDto, long postId);

    void deleteLikeFromPost(@Valid LikeDto likeDto, long postId);

    void addLikeToComment(@Valid LikeDto likeDto, long commentId);

    void deleteLikeFromComment(@Valid LikeDto likeDto, long commentId);

    List<LikeDto> findLikesOfPublishedPost(long postId);
}
