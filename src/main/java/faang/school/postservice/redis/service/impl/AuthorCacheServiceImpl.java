package faang.school.postservice.redis.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.model.entity.AuthorCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.service.AuthorCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthorCacheServiceImpl implements AuthorCacheService {

    @Value("${cache.author-ttl:86400}")
    private long authorTtl;

    @Value("${cache.author.prefix}")
    private String cachePrefix;

    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    @Autowired
    public AuthorCacheServiceImpl (AuthorCacheRedisRepository authorCacheRedisRepository,
                                   @Qualifier("redisCacheTemplate") RedisTemplate<String, Object> redisTemplate,
                                   AuthorCacheMapper authorCacheMapper, 
                                   UserServiceClient userServiceClient) {

        this.authorCacheRedisRepository = authorCacheRedisRepository;
        this.redisTemplate = redisTemplate;
        this.authorCacheMapper = authorCacheMapper;
        this.userServiceClient = userServiceClient;
    }
    
    public void saveAuthorToCache(Long postAuthorId) {
        UserDto author = userServiceClient.getUser(postAuthorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        authorCacheRedisRepository.save(authorCache);

        String key = createAuthorCacheKey(postAuthorId);

        redisTemplate.expire(key, Duration.ofSeconds(authorTtl));

        log.info("Author with ID {} saved to cache with key: {} and TTL: {} seconds", postAuthorId, key, authorTtl);
    }

    private String createAuthorCacheKey(Long postAuthorId) {
        return cachePrefix + postAuthorId;
    }

    @Override
    public CompletableFuture<Void> saveAllAuthorsToCache(List<UserDto> usersRange) {
        if (usersRange == null || usersRange.isEmpty()) {
            log.info("No users to cache.");
            return CompletableFuture.completedFuture(null);
        }

        List<AuthorCache> newAuthorCaches = filterNewAuthors(usersRange).stream()
                .map(authorCacheMapper::toAuthorCache)
                .toList();

        if (!newAuthorCaches.isEmpty()) {
            log.info("Saving {} new authors to cache.", newAuthorCaches.size());
            return CompletableFuture.runAsync(() -> {
                authorCacheRedisRepository.saveAll(newAuthorCaches);
                setTtlForAuthors(newAuthorCaches);
            });
        }

        return CompletableFuture.completedFuture(null);
    }

    private List<UserDto> filterNewAuthors(List<UserDto> usersRange) {
        List<String> keys = usersRange.stream()
                .map(user -> "author:" + user.getId())
                .toList();

        List<Object> results = redisTemplate.opsForValue().multiGet(keys);

        if (results == null) {
            log.warn("Failed to retrieve keys from Redis. Assuming all authors are new.");
            return usersRange;
        }

        return usersRange.stream()
                .filter(user -> {
                    int index = usersRange.indexOf(user);
                    return results.get(index) == null;
                })
                .toList();
    }

    private void setTtlForAuthors(List<AuthorCache> newAuthorCaches) {
        newAuthorCaches.forEach(authorCache -> {
            String key = createAuthorCacheKey(authorCache.getId());
            redisTemplate.expire(key, Duration.ofSeconds(authorTtl));
            log.info("Set TTL for author {} in cache.", authorCache.getId());
        });
    }
}
