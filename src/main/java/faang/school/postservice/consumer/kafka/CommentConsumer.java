package faang.school.postservice.consumer.kafka;

import faang.school.postservice.cache.redis.PostCache;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentConsumer {
    private final PostCache postCache;
    private final PostMapper postMapper;
    private final PostService postService;
    private final CommentService commentService;

    @Value("${spring.data.redis.max-comments-size}")
    private int maxSize;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics-name.comments}", containerFactory = "containerFactory")
    public void listenCommentEvent(CommentEvent commentEvent, Acknowledgment ack) {
        long postId = commentEvent.getPostId();
        long commentId = commentEvent.getCommentId();
        CommentDto commentDto = findCommentById(commentId);
        CachedPostDto cachedPostDto = findPostById(postId);
        cachedPostDto.addNewComment(commentDto, maxSize);
        postCache.save(cachedPostDto);
        log.info("added comment to post with id = {}", postId);
        ack.acknowledge();
    }

    private CommentDto findCommentById(long commentId) {
        try {
            return commentService.findById(commentId);
        } catch (EntityNotFoundException e) {
            log.info("comment with id = {} not exist", commentId, e);
            throw new NonRetryableException(String.format("комментария с id = %d не сущетсвует ", commentId));
        }
    }

    private CachedPostDto findPostById(long postId) {
        try {
            return postCache.findById(postId)
                    .orElse(postMapper.toCachedPostDto(postService.getPostById(postId)));
        } catch (EntityNotFoundException e) {
            log.info("post with id = {} not exist", postId);
            throw new NonRetryableException(String.format("поста с id = %d не сущетсвует ", postId));
        }
    }
}