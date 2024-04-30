package faang.school.postservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.Set;

/**
 * @author Alexander Bulgakov
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private static final String POST_COMMENTS_KEY_PREFIX = "post:comments:";
    @Value("${spring.redis.comments.max-comments}")
    private final int maxComments;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostRepository redisPostRepository;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.comments}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CommentAddEvent commentAddEvent, Acknowledgment acknowledgment) {
        RedisPost post = getPost(commentAddEvent.postId());

        try {
            processEvent(commentAddEvent, post);
        } catch (JsonProcessingException e) {
            log.error("");
        }

        acknowledgment.acknowledge();
    }

    private void processEvent(CommentAddEvent commentAddEvent, RedisPost post) throws JsonProcessingException {
        String postCommentsKey = POST_COMMENTS_KEY_PREFIX + commentAddEvent.postId();
        long timestamp = commentAddEvent.createdAt().toEpochSecond(ZoneOffset.UTC);
        Comment comment = commentService.getComment(commentAddEvent.commentId());

        ZSetOperations<String, Object> comments = redisTemplate.opsForZSet();
        String commentJson = objectMapper.writeValueAsString(comment);
        comments.add(postCommentsKey, commentJson, timestamp);

        post.getComments().add(comment);

        redisPostRepository.save(post);

        ensureMaxComments(comments, postCommentsKey, maxComments);
    }

    private void ensureMaxComments(ZSetOperations<String, Object> zSetOps, String postCommentsKey, int maxComments) {
        Long currentSize = zSetOps.size(postCommentsKey);
        if (currentSize != null && currentSize > maxComments) {
            Set<Object> oldestComments = zSetOps.range(postCommentsKey, 0, currentSize - maxComments - 1);
            if (oldestComments != null) {
                zSetOps.remove(postCommentsKey, oldestComments.toArray());
            }
        }
    }

    private RedisPost getPost(long postId) {
        return redisPostRepository.findById(postId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Post not found by Id: %d", postId)));
    }
}
