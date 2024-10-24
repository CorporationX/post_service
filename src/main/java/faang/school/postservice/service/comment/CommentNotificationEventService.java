package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentNotificationEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentNotificationEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentNotificationEventService {
    private final PostService postService;
    private final RedisCommentNotificationEventPublisher commentNotificationEventPublisher;

    public void handleCommentEvent(Long postId, Comment savedComment) {
        Post post = postService.findPostById(postId);

        if (!post.getAuthorId().equals(savedComment.getAuthorId())) {
            CommentNotificationEvent event = new CommentNotificationEvent(
                    postId,
                    savedComment.getId(),
                    post.getAuthorId(),
                    savedComment.getAuthorId(),
                    savedComment.getContent()
            );
            commentNotificationEventPublisher.publishCommentNotificationEvent(event);
        }
    }
}