package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersLikesByPostId(long postId);

    List<UserDto> getUsersLikesByCommentId(long commentId);
}