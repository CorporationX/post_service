package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.AuthorMapper;
import faang.school.postservice.model.redis.AuthorRedisCache;
import faang.school.postservice.property.CacheProperty;
import faang.school.postservice.repository.redis.AuthorRedisRepository;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorRedisCacheService implements RedisCacheService<AuthorRedisCache, Long> {

    @Value("${spring.cache.cache-settings.users.name}")
    private String cacheName;
    private int ttl;
    private final CacheProperty cacheProperty;
    private final UserServiceClient userServiceClient;
    private final AuthorRedisRepository authorRedisRepository;
    private final AuthorMapper authorMapper;

    @PostConstruct
    public void init() {
        ttl = cacheProperty.getCacheSettings().get(cacheName).getTtl();
    }

    @Override
    @Retryable(retryFor = {FeignException.class, OptimisticEntityLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public AuthorRedisCache save(AuthorRedisCache entity) {

        UserDto userDto = userServiceClient.getUser(entity.getId());
        AuthorRedisCache redisUser = authorMapper.toAuthorCache(userDto);
        redisUser.setTtl(ttl);

        redisUser = authorRedisRepository.save(redisUser);

        return redisUser;
    }

    @Override
    public void delete(Long id) {

        authorRedisRepository.deleteById(id);
        log.info("Removed author with id {} from cache", id);
    }

    @Override
    public AuthorRedisCache findById(Long id) {

        return authorRedisRepository.findById(id).orElse(null);
    }
}
