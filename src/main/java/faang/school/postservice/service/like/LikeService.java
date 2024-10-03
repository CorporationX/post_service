package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {

    LikeDto likePost(LikeDto likeDto);

    void unlikePost(LikeDto likeDto);

    LikeDto likeComment(LikeDto likeDto);

    void unlikeComment(LikeDto likeDto);

    List<UserDto> getUsersLikedPost(long postId);

    List<UserDto> getUsersLikedComment(long commentId);
}

