package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@CacheConfig(cacheNames = "users")
public class UserRedisRepository {

    @CachePut(key = "#userDto.id")
    public UserDto saveUser(UserDto userDto) {
        return userDto;
    }

    @Cacheable(key = "#authorId", unless = "#result == null")
    public UserDto findById(Long authorId) {
        log.warn("Not found cache with author id {}", authorId);
        return null;
    }
}
