package faang.school.postservice.repository.redis.post;

import faang.school.postservice.model.redis.PostCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;

@Slf4j
public class PostCacheRepositoryCustomImpl implements PostCacheRepositoryCustom {
    private final RedisKeyValueTemplate keyValueTemplate;

    public PostCacheRepositoryCustomImpl(RedisKeyValueTemplate keyValueTemplate) {
        this.keyValueTemplate = keyValueTemplate;
    }

    @Override
    public boolean saveIfAbsent(PostCache postCache) {
        log.info("Adding post ID: {} to cache.", postCache.getId());
        try {
            keyValueTemplate.insert(postCache);
            return true;
        }  catch (DataIntegrityViolationException e) {
            // Key already exists — insert fails, and that's OK
            return false;
        } catch (Exception e) {
            log.warn("Unexpected error while inserting postCache: {}", postCache.getId(), e);
            throw new IllegalStateException("Unexpected Redis insert failure", e);
        }
    }

}
