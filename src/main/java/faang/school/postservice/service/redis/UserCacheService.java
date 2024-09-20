package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.UserForCache;
import faang.school.postservice.repository.redis.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;

    public void save(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        UserForCache userForSaveToCache = userMapper.toUserForCache(userDto);
        userCacheRepository.save(userForSaveToCache);
    }
}
