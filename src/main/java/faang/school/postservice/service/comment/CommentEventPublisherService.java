package faang.school.postservice.service.comment;

import faang.school.postservice.config.redis.RedisCommentEventPublisher;
import faang.school.postservice.config.redis.dto.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisherService {

    private final RedisCommentEventPublisher publisher;

    @Async
    public void publishAsync(Comment comment, Post post) {
        try {
            CommentEvent event = new CommentEvent(
                    comment.getId(),
                    post.getAuthorId(),
                    comment.getAuthorId(),
                    post.getId(),
                    comment.getContent()
            );
            publisher.publish(event);
            log.info("CommentEvent опубликован асинхронно: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish comment event asynchronously", e);
        }
    }
}