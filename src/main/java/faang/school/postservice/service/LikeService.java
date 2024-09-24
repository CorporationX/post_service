package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface LikeService {
    void addLikeToPost(@Valid LikeDto likeDto, @NotNull long postId);

    void deleteLikeFromPost(@Valid LikeDto likeDto, @NotNull long postId);

    void addLikeToComment(@Valid LikeDto likeDto, @NotNull long commentId);

    void deleteLikeFromComment(@Valid LikeDto likeDto, @NotNull long commentId);

    List<LikeDto> findLikesOfPublishedPost(@NotNull long postId);
}
