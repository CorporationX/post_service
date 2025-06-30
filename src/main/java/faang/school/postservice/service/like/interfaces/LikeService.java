package faang.school.postservice.service.like.interfaces;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {

    LikeDto likePost(long postId);

    void unlikePost(long postId);

    LikeDto likeComment(long commentId);

    void unlikeComment(long commentId);

    List<UserDto> getUserLikedPost(long postId);

    List<UserDto> getUserLikedComment(long commentId);

}
