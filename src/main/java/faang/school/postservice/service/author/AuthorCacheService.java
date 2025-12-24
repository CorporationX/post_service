package faang.school.postservice.service.author;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.author.AuthorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.ttl.author-post}")
    private Long ttl;

    public Optional<AuthorDto> get(String authorId) {
        String key = "author:" + authorId;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(json, AuthorDto.class));
        } catch (Exception e) {
            log.warn("Failed to deserialize author from cache for id={}", authorId, e);
            return Optional.empty();
        }
    }

    public void put(AuthorDto author) {
        String key = "author:" + author.id();
        try {
            redisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(author),
                    ttl, TimeUnit.DAYS
            );
        } catch (Exception e) {
            log.warn("Failed to cache author: {}", author, e);
        }
    }
}
