package faang.school.postservice.service.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.entity.redis.Users;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUserService {
    private final RedisUserRepository redisUserRepository;
    @Value("${spring.data.redis.cache.ttl.users}")
    private long ttlUsers;

    public void save(long userId, UserDto userDto){
        redisUserRepository.save(Users.builder()
                .id(userId)
                .userDto(userDto)
                .ttlUsers(ttlUsers)
                .build());
    }
}
