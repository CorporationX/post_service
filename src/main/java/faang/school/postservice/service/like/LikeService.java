package faang.school.postservice.service.like;

import faang.school.postservice.dto.LikeCountDto;
import faang.school.postservice.dto.LikeDto;

import java.util.List;

public interface LikeService {
    LikeDto putLikeToPost(long postId);

    LikeDto putLikeToComment(long commentId);

    void deleteLikeForPost(long postId);

    void deleteLikeForComment(long commentId);

    LikeCountDto countLikesForPost(Long postId);

    List<LikeDto> getLikesByUser();
}
