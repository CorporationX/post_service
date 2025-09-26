package faang.school.postservice.newsfeed.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommentCacheService {

    private final RedisTemplate<String, Object> genericRedisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${comment.cache.max-size:3}")
    private int maxComments;

    public void saveComment(CommentEvent comment) {
        String zsetKey = "post:" + comment.postId() + ":comments";
        String hashKey = "comment:" + comment.commentId();

        try {
            String json = objectMapper.writeValueAsString(comment);

            genericRedisTemplate.opsForHash().put(hashKey, "data", json);

            double score = (double) System.currentTimeMillis();
            genericRedisTemplate.opsForZSet().add(zsetKey, comment.commentId(), score);

            Long size = genericRedisTemplate.opsForZSet().size(zsetKey);
            if (size != null && size > maxComments) {
                long end = size - maxComments - 1;
                genericRedisTemplate.opsForZSet().removeRange(zsetKey, 0, end);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize comment {} for Redis", comment.commentId(), e);
            throw new RuntimeException(e);
        }
    }
}