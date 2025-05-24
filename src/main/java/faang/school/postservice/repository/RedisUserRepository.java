package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "users")
public class RedisUserRepository {

    @CachePut(key = "#userDto.id")
    public UserDto saveUser(UserDto userDto) {
        return userDto;
    }
}
