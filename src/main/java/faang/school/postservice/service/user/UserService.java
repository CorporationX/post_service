package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.entity.AuthorRedisCache;

public interface UserService {

    UserDto getUserById(long userId);

    AuthorRedisCache getUserAuthorCacheById(long userId);
}
