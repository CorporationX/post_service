package faang.school.postservice.repository.redis.post;

import faang.school.postservice.mapper.PostCacheMapper;
import faang.school.postservice.model.redis.PostCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class PostCacheRepositoryCustomImpl implements PostCacheRepositoryCustom {

    @Autowired
    @Qualifier("rawHashRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PostCacheMapper postCacheMapper;

    public List<PostCache> getMany(List<Long> ids) {
        List<Object> rawHashes = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long id : ids) {
                String redisKey = getKey(id);
                connection.hGetAll(redisTemplate.getStringSerializer().serialize(redisKey));
            }
            return null;
        });

        List<PostCache> result = new ArrayList<>();

        for (Object raw : rawHashes) {
            if (!(raw instanceof Map<?, ?> map) || map.isEmpty()) {
                result.add(null);
                continue;
            }

            Map<String, String> fieldMap = (Map<String, String>) map;

            try {
                PostCache post = postCacheMapper.fromHash(fieldMap);

                result.add(post);

            } catch (Exception e) {
                log.error("Failed to parse PostCache from Redis hash: {}", fieldMap, e);
            }
        }

        return result;
    }

    private String getKey(long postId){
        String keyPrefix = "Post:";
        return keyPrefix + postId;
    }

}
