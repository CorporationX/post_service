package faang.school.postservice.service;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserFromCacheOrService(Long userId);

    List<UserDto> getUsersFromCacheOrService(List<Long> userIds);
}
