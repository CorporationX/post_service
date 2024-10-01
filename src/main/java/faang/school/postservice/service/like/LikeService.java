package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersLikedPost(long postId);

    List<UserDto> getUsersLikedComment(long commentId);
}
