package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRedisRepository<D> implements RedisRepository<String, String, D> {

    protected final StringRedisTemplate redisTemplate;
    protected final ObjectMapper objectMapper;
    protected final Utils utils;

    @Override
    public void save(D d) {
        String key = add(d);
        long ttl = getTimeToLive();
        if (ttl > 0) {
            setTtl(redisTemplate, key, getTimeToLive());
        }
    }

    protected abstract long getTimeToLive();

    /**
     * Реализация процесса сохранения объекта в redis
     *
     * @param d - dto объекта который должны использовать для сохранения
     * @return ключ объекта, с которым работает репозиторий
     */
    protected abstract String add(D d);

    protected String getKey(String prefix, String id) {
        return utils.format(prefix, id);
    }

    protected String getJsonText(D d) {
        try {
            return objectMapper.writeValueAsString(d);
        } catch (JsonProcessingException e) {
            log.error("get json text error: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
