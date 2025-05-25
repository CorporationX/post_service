package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.RedisCommentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;

import static faang.school.postservice.contants.ErrorMessage.ERROR_SERIALIZE_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommentServiceImpl implements RedisCommentService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper mapper;
    private final String redisKeyPrefix = "post:%d:comment";

    @Value("${app.redis.max-comment-cache:3}")
    private int maxCommentCache;

    public void saveComment(CommentEvent event) {
        String redisKey = String.format(redisKeyPrefix, event.getPostId());
        double score = (double) Instant.now().toEpochMilli();
        try {
            String json = mapper.writeValueAsString(event);
            redisTemplate.opsForZSet().add(redisKey, json, score);

            Long size = redisTemplate.opsForZSet().zCard(redisKey);
            if (size != null && size > maxCommentCache) {
                long toRemove = size - maxCommentCache;
                redisTemplate.opsForZSet().removeRange(redisKey, 0, toRemove - 1);
            }
        } catch (JsonProcessingException e) {
            String errorMessage = String.format(ERROR_SERIALIZE_EVENT, event.getClass().getSimpleName());
            log.error(errorMessage, e);
            throw new RedisCommentException(errorMessage);
        }
    }
}