package faang.school.postservice.cache.comment;

import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import org.springframework.data.redis.core.RedisTemplate;

public interface PostCommentCache {
    boolean postCached(long postId, RedisTemplate<String, ?> postTemplate, String postKeyPrefix);
    boolean addLast(long postId, FeedCommentCacheDto entry);
}
