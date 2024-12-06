package faang.school.postservice.repository.comment;

import faang.school.postservice.properties.redis.comment.RedisCommentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisCommentProperties redisCommentProperties;


}
