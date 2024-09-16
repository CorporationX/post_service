package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface LikeService {
    List<UserDto> getUsersLikedPost(long postId);

    List<UserDto> getUsersLikedComm(long postId);
}
