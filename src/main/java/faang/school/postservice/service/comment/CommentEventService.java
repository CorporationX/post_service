package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentEventService {
    private final PostService postService;
    private final RedisCommentEventPublisher commentEventPublisher;

    public void handleCommentEvent(Long postId, Comment savedComment) {
        Post post = postService.findPostById(postId);

        if (!post.getAuthorId().equals(savedComment.getAuthorId())) {
            CommentEvent event = new CommentEvent(
                    postId,
                    savedComment.getAuthorId(),
                    savedComment.getId(),
                    LocalDateTime.now()
            );
            commentEventPublisher.publishCommentEvent(event);
        }
    }
}