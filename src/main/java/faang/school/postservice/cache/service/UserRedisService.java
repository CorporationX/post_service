package faang.school.postservice.cache.service;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRedisService {
    private final UserRedisRepository userRedisRepository;

    public List<UserRedis> getAllByIds(Iterable<Long> ids) {
        Iterable<UserRedis> userRedisIterable = userRedisRepository.findAllById(ids);
        return StreamSupport.stream(userRedisIterable.spliterator(), false)
                .toList();
    }

    public void save(UserDto userDto) {
        if (!existsById(userDto.getId())) {
            UserRedis userRedis = new UserRedis(userDto.getId(), userDto.getUsername());
            save(userRedis);
        }
    }

    public void saveAll(Iterable<UserRedis> usersRedis) {
        userRedisRepository.saveAll(usersRedis);
    }

    public boolean existsById(Long id) {
        return userRedisRepository.existsById(id);
    }

    private void save(UserRedis userRedis) {
        userRedisRepository.save(userRedis);
        log.info("User by id {} saved to cache", userRedis.getId());
    }
}
