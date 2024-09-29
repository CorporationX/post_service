package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.like.LikeDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersLikedPost(long postId);

    void addLikeToPost(LikeDto likeDto, long postId);

    void deleteLikeFromPost(LikeDto likeDto, long postId);

    List<UserDto> getUsersLikedComm(long postId);

    void addLikeToComment(LikeDto likeDto, long commentId);

    void deleteLikeFromComment(LikeDto likeDto, long commentId);

    List<LikeDto> findLikesOfPublishedPost(long postId);
}
