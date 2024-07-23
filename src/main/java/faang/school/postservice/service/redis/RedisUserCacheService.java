package faang.school.postservice.service.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.RedisUser;

import java.util.Optional;

public interface RedisUserCacheService {

    RedisUser save(UserDto userDto);

    Optional<RedisUser> get(long userId);

    void deleteUserById(long userId);
}
