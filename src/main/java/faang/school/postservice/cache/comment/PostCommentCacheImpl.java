package faang.school.postservice.cache.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.cache.comment.CommentCacheProperties;
import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCommentCacheImpl implements PostCommentCache {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CommentCacheProperties properties;

    private String listKey(long postId) {
        return properties.listKeyPrefix() + postId;
    }

    private String idsKey(long postId) {
        return properties.idsKeyPrefix() + postId;
    }

    private String setKey(long postId) {
        return properties.setKeyPrefix() + postId;
    }

    @Override
    public boolean postCached(long postId,
                              RedisTemplate<String, ?> postTemplate,
                              String postKeyPrefix) {
        try {
            return Boolean.TRUE.equals(postTemplate.hasKey(postKeyPrefix + postId));
        } catch (Exception e) {
            log.warn("Redis hasKey failed for postId={}", postId, e);
            return false;
        }
    }

    @Override
    public boolean addLast(long postId, FeedCommentCacheDto entry) {
        String listKey = listKey(postId);
        String idsKey = idsKey(postId);
        String setKey = setKey(postId);
        String cid = String.valueOf(entry.id());

        try {
            Boolean firstTime = redisTemplate.opsForHash().putIfAbsent(setKey, cid, "1");
            if (Boolean.FALSE.equals(firstTime)) {
                return false;
            }

            String json = objectMapper.writeValueAsString(entry);
            redisTemplate.opsForList().leftPush(listKey, json);
            redisTemplate.opsForList().leftPush(idsKey, cid);

            int max = properties.maxSize();
            redisTemplate.opsForList().trim(listKey, 0, max - 1);
            redisTemplate.opsForList().trim(idsKey, 0, max - 1);
            return true;
        } catch (Exception e) {
            log.warn("Redis addLast comment failed postId={} commentId={}", postId, entry.id(), e);
            return false;
        }
    }

    @Override
    public List<FeedCommentCacheDto> getLast(long postId, int limit) {
        String listKey = listKey(postId);
        try {
            List<String> raw = redisTemplate.opsForList().range(listKey, 0, Math.max(0, limit - 1));
            if (raw == null || raw.isEmpty()) {
                return List.of();
            }

            List<FeedCommentCacheDto> out = new ArrayList<>(raw.size());
            for (String s : raw) {
                out.add(objectMapper.readValue(s, FeedCommentCacheDto.class));
            }
            return out;
        } catch (Exception e) {
            log.warn("Redis getLast comments failed postId={}", postId, e);
            return List.of();
        }
    }
}
