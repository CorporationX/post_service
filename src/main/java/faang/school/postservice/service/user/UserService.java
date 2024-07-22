package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.entity.AuthorCache;

public interface UserService {

    UserDto getUserById(long userId);

    AuthorCache getUserAuthorCacheById(long userId);
}
