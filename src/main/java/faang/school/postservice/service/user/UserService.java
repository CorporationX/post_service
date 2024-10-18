package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserById(long userId);

    List<UserDto> getAllUsers();
}
