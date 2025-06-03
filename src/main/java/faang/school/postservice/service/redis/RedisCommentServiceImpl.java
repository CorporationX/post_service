package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.RedisCommentException;
import faang.school.postservice.utils.json.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import org.springframework.data.redis.connection.ReturnType;

import static faang.school.postservice.contants.ErrorMessage.ERROR_SERIALIZE_EVENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCommentServiceImpl implements RedisCommentService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonUtils jsonUtils;

    @Value("${app.redis.max-comment-cache:3}")
    private int maxCommentCache;

    @Value("${spring.data.redis.key-prefix.comment}")
    private String redisKeyPrefix;

    @Value("${spring.data.redis.script.commentCache}")
    private String commentCacheLuaScript;

    public void saveComment(CommentEvent event) {
        String redisKey = String.format(redisKeyPrefix, event.getPostId());
        double score = (double) Instant.now().toEpochMilli();
        try {
            String json = jsonUtils.toJson(event);

            redisTemplate.execute((RedisCallback<Object>) (connection) -> {
                return connection.eval(
                        commentCacheLuaScript.getBytes(),
                        ReturnType.VALUE,
                        1,
                        redisKey.getBytes(),
                        String.valueOf(score).getBytes(),
                        json.getBytes(),
                        String.valueOf(maxCommentCache).getBytes()
                );
            });
        } catch (RuntimeException e) {
            String errorMessage = String.format(ERROR_SERIALIZE_EVENT, event.getClass().getSimpleName());
            log.error(errorMessage, e);
            throw new RedisCommentException(errorMessage);
        }
    }
}