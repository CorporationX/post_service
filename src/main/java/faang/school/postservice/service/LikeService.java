package faang.school.postservice.service;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface LikeService {
    List<UserDto> getAllUsersLikedPost(long postId);

    List<UserDto> getAllUsersLikedComment(long commentId);

    LikeDto addLikeToPost(Long postId, LikeDto likeDto);

    LikeDto removeLikeFromPost(Long postId, LikeDto likeDto);

    LikeDto addLikeToComment(Long commentId, LikeDto likeDto);

    LikeDto removeLikeFromComment(Long commentId, LikeDto likeDto);

    List<Long> getLikesFromPost(Long postId);

    List<Long> getLikesFromComment(Long commentId);

    int getLikeCount(Long postId);

    Map<Long, Integer> getPostIdLikeCountMap(List<Long> postIds);
}
