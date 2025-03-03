package faang.school.postservice.service.feed;

import faang.school.postservice.model.feed.UserCache;
import faang.school.postservice.repository.feed.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUserService {

    private final RedisTemplate<String, UserCache> redisTemplate;
    private final RedisUserRepository redisUserRepository;

    public UserCache save(UserCache user) {
        return redisUserRepository.save(user);
    }

    public Optional<UserCache> findUserById(Long userId) {
        return redisUserRepository.findById(userId.toString());
    }
}
