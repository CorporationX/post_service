package faang.school.postservice.service.redis.impl;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.redis.RedisUserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUserCacheServiceImpl implements RedisUserCacheService {

    @Value("${spring.data.redis.cache.ttl}")
    private int userTtl;

    private final RedisUserRepository redisUserRepository;
    private final  RedisUserMapper redisUserMapper;
    private final RedisKeyValueTemplate redisTemplate;

    @Override
    public RedisUser save(UserDto userDto) {
        RedisUser redisUser = redisUserMapper.toEntity(userDto);
        redisUser.setTtl(userTtl);

        redisUserRepository.findById(userDto.getId()).ifPresentOrElse(
                post -> redisTemplate.update(redisUser),
                () -> redisUserRepository.save(redisUser)
        );
        return redisUser;
    }

    @Override
    public Optional<RedisUser> get(long userId) {
        return redisUserRepository.findById(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        redisUserRepository.deleteById(userId);
    }
}
