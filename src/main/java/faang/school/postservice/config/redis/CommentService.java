package faang.school.postservice.config.redis;

import faang.school.postservice.config.redis.dto.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CommentService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author agent
 * @since 13.08.2025
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final RedisCommentEventPublisher publisher;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long addComment(Long postId, Long commentAuthorId, String text) {
        Post post = postRepository.getRequiredById(postId);

        Comment comment = new Comment();
        comment.setAuthorId(commentAuthorId);
        comment.setPost(post);
        comment.setContent(text);

        Comment commentSaved = commentRepository.save(comment);

        publisher.publish(new CommentEvent(
                commentSaved.getId(),
                post.getAuthorId(),
                commentAuthorId,
                postId,
                text
        ));

        return commentSaved.getId();
    }
}