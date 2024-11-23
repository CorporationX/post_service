package faang.school.postservice.repository.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.redis.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CachePostRepository implements CacheRepository<PostCacheDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final int ttl;
    private final String key;

    public CachePostRepository(RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper objectMapper,
                               RedisProperties redisProperties) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        ttl = redisProperties.getPost().getTtl();
        key = redisProperties.getPost().getKey();
    }

    @Override
    public void save(PostCacheDto postCacheDto) {
        log.debug("Repository save() start, postCacheDto-{}", postCacheDto);

        redisTemplate.opsForValue()
                .set(getKeyName(postCacheDto.getId()), postCacheDto, ttl, TimeUnit.SECONDS);

        log.debug("Repository save() finish, postCacheDto-{}", postCacheDto);
    }

    public PostCacheDto get(long key) {
        log.debug("Repository get() start, key-{}", key);

        Optional<Object> object = Optional.ofNullable(redisTemplate.opsForValue()
                .get(getKeyName(key)));

        if (object.isEmpty()) {
            throw new NoSuchElementException("Element not found");
        }

        PostCacheDto postCacheDto = objectMapper.convertValue(object.get(), PostCacheDto.class);

        log.debug("Repository get() finish, key-{}, postCacheDto-{}", key, postCacheDto);
        return postCacheDto;
    }

    private String getKeyName(long id) {
        return key.concat(String.valueOf(id));
    }
}
