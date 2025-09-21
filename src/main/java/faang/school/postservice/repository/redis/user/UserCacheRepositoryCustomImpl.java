package faang.school.postservice.repository.redis.user;

import faang.school.postservice.model.redis.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;

@Slf4j
public class UserCacheRepositoryCustomImpl implements UserCacheRepositoryCustom {
    private final RedisKeyValueTemplate keyValueTemplate;

    public UserCacheRepositoryCustomImpl(RedisKeyValueTemplate keyValueTemplate) {
        this.keyValueTemplate = keyValueTemplate;
    }

    @Override
    public boolean saveIfAbsent(UserCache userCache) {
        try {
            keyValueTemplate.insert(userCache);
            return true;
        }  catch (DataIntegrityViolationException e) {
            // Key already exists — insert fails, and that's OK
            return false;
        } catch (Exception e) {
            log.warn("Unexpected error while inserting userCache: {}", userCache.getId(), e);
            throw new IllegalStateException("Unexpected Redis insert failure", e);
        }
    }

}
