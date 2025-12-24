package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<Long> getNotBannedUsersIds(List<Long> ids);

    UserDto getUser(Long userId);
}