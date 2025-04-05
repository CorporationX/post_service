package faang.school.postservice.service.user;

import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    UserDto getUserWithCache(long userId);

    List<SubscriptionUserDto> getFollowersAsync(long userId);

    List<SubscriptionUserDto> getFollowers(long userId);

    List<UserResponseDto> getAllUsers();

}
