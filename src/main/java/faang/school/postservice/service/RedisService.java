package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final UserServiceClient userServiceClient;
    private final UserRedisRepository userRedisRepository;
    private final UserMapper userMapper;

    //make retryable
    public void cacheUserById (long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        userRedisRepository.save(userMapper.toRedisDto(userDto));
    }
}
