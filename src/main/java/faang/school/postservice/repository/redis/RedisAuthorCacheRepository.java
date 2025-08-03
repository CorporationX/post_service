package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.CachedAuthor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisAuthorCacheRepository {
    private final static String PREFIX = "author_";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.cache.author_cache_ttl_hours}")
    private long ttlHours;
    private HashOperations<String, String, Object> hashOpsAuthors;

    @PostConstruct
    public void init() {
        hashOpsAuthors = redisTemplate.opsForHash();
    }

    public void saveAuthor(CachedAuthor author) {
        try {
            String json = objectMapper.writeValueAsString(author);
            hashOpsAuthors.put(PREFIX + author.getId(), author.getId().toString(), json);
            redisTemplate.expire(PREFIX + author.getId(), ttlHours, TimeUnit.HOURS);
            log.debug("Author with id: {} was saved in Cache", author.getId());
        } catch (Exception e) {
            log.error("Something went wrong with save author with id: {} in Cache", author.getId());
        }
    }

    public CachedAuthor getAuthor(Long authorId) {
        try {
            Object authorData = hashOpsAuthors.get(PREFIX + authorId, authorId.toString());
            if (authorData == null) {
                log.warn("Author with id: {} was not found", authorId);
                return null;
            }
            return objectMapper.readValue(authorData.toString(), CachedAuthor.class);
        } catch (JsonMappingException ex) {
            String message = "Json Mapping for author with id: %d was failed".formatted(authorId);
            log.error(message);
            throw new RuntimeException(message, ex);
        } catch (JsonProcessingException ex) {
            String message = "Something with Json for author with id: %d went wrong".formatted(authorId);
            log.error(message);
            throw new RuntimeException(message, ex);
        }
    }
}