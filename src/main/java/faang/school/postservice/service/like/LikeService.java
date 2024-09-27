package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersByPostId(long postId);

    List<UserDto> getUsersByCommentId(long commentId);
}
