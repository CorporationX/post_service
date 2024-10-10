package faang.school.postservice.service.redis;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserRedisService {
    private final UserRedisRepository userRedisRepository;

    public List<UserRedis> getAllByIds(Iterable<Long> ids) {
        Iterable<UserRedis> userRedisIterable = userRedisRepository.findAllById(ids);
        return StreamSupport.stream(userRedisIterable.spliterator(), false)
                .toList();
    }

    public void save(UserRedis userRedis) {
        userRedisRepository.save(userRedis);
    }

    public void save(UserDto userDto) {
        if (!userRedisRepository.existsById(userDto.getId())) {
            UserRedis userRedis = new UserRedis(userDto.getId(), userDto.getUsername());
            save(userRedis);
        }
    }

    public boolean existsById(Long id) {
        return userRedisRepository.existsById(id);
    }
}
