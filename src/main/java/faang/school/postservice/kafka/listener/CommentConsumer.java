package faang.school.postservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.cache.comment.PostCommentCacheImpl;
import faang.school.postservice.config.properties.cache.post.PostCacheProperties;
import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.comment.CommentFeedEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentConsumer {

    private final ObjectMapper objectMapper;
    private final PostCommentCacheImpl commentCache;
    private final RedisTemplate<String, PostCacheDto> postTemplate;
    private final PostCacheProperties postCacheProperties;
    private final CommentMapper mapper;

    @KafkaListener(
            topics = "${feed.kafka.topics.comment}"
    )
    public void onComment(String message) {
        CommentFeedEvent event;
        try {
            event = objectMapper.readValue(message, CommentFeedEvent.class);
        } catch (Exception parseEx) {
            log.error("Failed to parse comment event payload: {}", message, parseEx);
            return;
        }

        long postId = event.postId();
        if (!commentCache.postCached(postId, postTemplate, postCacheProperties.keyPrefix())) {
            return;
        }
        FeedCommentCacheDto entry = mapper.toCacheEntry(event);
        boolean saved = commentCache.addLast(postId, entry);

        if (!saved) {
            throw new IllegalStateException("Comment was not cached (will retry). postId="
                    + postId + " commentId=" + entry.id());
        }
    }
}
