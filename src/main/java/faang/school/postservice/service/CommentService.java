package faang.school.postservice.service;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentEventPublisher commentEventPublisher;

    @Transactional
    public Comment createComment(Long postId, Long authorId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .authorId(authorId)
                .content(content)
                .build();

        comment = commentRepository.save(comment);

        // Отправляем событие только если комментарий не от автора поста
        if (!authorId.equals(post.getAuthorId())) {
            CommentEvent event = new CommentEvent(
                    comment.getId(),
                    authorId,
                    postId,
                    post.getAuthorId(),
                    content,
                    LocalDateTime.now()
            );
            commentEventPublisher.publishCommentEvent(event);
        }

        return comment;
    }
}
