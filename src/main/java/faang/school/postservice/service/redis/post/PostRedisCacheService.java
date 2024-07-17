package faang.school.postservice.service.redis.post;

import faang.school.postservice.model.redis.PostRedisCache;
import faang.school.postservice.property.CacheProperty;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.service.redis.RedisCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostRedisCacheService implements RedisCacheService<PostRedisCache, Long> {

    @Value("${spring.cache.cache-settings.posts.name}")
    private String cacheName;
    private int ttl;
    private final CacheProperty cacheProperty;
    private final PostRedisRepository postRedisRepository;

    @PostConstruct
    public void init() {
        ttl = cacheProperty.getCacheSettings().get(cacheName).getTtl();
    }

    @Override
    public PostRedisCache save(PostRedisCache entity) {

        entity.setTtl(ttl);

        postRedisRepository.findById(entity.getId()).ifPresent(postRedisRepository::delete);

        entity = postRedisRepository.save(entity);

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);

        return entity;
    }

    @Override
    public void delete(Long id) {

        postRedisRepository.deleteById(id);
        log.info("Removed author with id {} from cache", id);
    }

    @Override
    public PostRedisCache findById(Long id) {

        return postRedisRepository.findById(id).orElse(null);
    }
}
