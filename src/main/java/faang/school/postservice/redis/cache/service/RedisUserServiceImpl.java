package faang.school.postservice.redis.cache.service;

import faang.school.postservice.redis.cache.model.RedisUser;
import faang.school.postservice.redis.cache.repository.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisUserServiceImpl implements RedisUserService {
    private final RedisUserRepository redisUserRepository;

    @Override
    public void saveUser(RedisUser user) {
        redisUserRepository.save(user);
    }

    @Override
    public RedisUser findbyId(Long id) {
        return redisUserRepository.findById(id).orElseThrow(() -> {
            log.error("Could not find user with id {}", id);
            return new EntityNotFoundException(String.format("Could not find user wiwth id %d in Redis cache. Fetching from Db", id));
        });
    }
}
