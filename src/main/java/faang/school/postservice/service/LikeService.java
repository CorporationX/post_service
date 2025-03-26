package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> findAllUserWhoLikedPost(Long postId);
    List<UserDto> findAllUserWhoLikedComment(Long commentId);
}
