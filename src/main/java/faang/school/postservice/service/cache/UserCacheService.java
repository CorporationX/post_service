package faang.school.postservice.service.cache;

import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRedisRepository userRedisRepository;
    private final FeedProps feedProps;

    public Optional<String> findAuthor(Long userId, Long projectId) {
        log.info("Find author by id {} in cache", userId);
        long authorId = userId != null ? userId : projectId;
        return userRedisRepository.findById(authorId)
                .map(UserRedis::getUsername);
    }

    public void save(UserRedis userRedis) {
        userRedis.setTtl(feedProps.user().ttl());
        userRedisRepository.save(userRedis);
        log.info("Saved to cache userId = {}", userRedis.getId());
    }
}
