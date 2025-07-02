package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.comment.CommentValidationException;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.facade.comment.CommentKafkaFacade;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final PostService postService;
    private final UserContext userContext;
    private final CommentKafkaFacade commentKafkaFacade;

    @Transactional
    public Comment create(long postId, Comment comment) {
        long userId = userContext.getUserId();
        comment.setAuthorId(userId);

        commentValidator.validateCommentAuthor(userId);

        Post post = postService.getPostById(postId);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.info("Создан комментарий с id={}", comment.getId());

        if (post.getAuthorId() != null) {
            commentKafkaFacade.createCommentEvent(savedComment);
        }

        return savedComment;
    }

    @Transactional
    public Comment update(Comment comment) {
        commentValidator.validateCommentAuthor(comment.getAuthorId());

        long userId = userContext.getUserId();
        if (!Objects.equals(userId, comment.getAuthorId())) {
            throw new CommentValidationException("Обновление разрешено только автору комментария");
        }

        Comment updatedComment = commentRepository.save(comment);
        log.debug("Обновлен комментарий с id={}", comment.getId());
        return updatedComment;
    }

    @Transactional(readOnly = true)
    public Comment get(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException(commentId);
        }

        commentRepository.deleteById(commentId);
        log.debug("Комментарий с id={} успешно удален", commentId);
    }

    @Transactional
    public List<Comment> fetchCommentsForModeration(int limit) {
        return commentRepository.lockAndFetchUnverified(limit);
    }

    @Transactional
    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Transactional
    public void save(Comment comment) {
        commentRepository.save(comment);
    }
}
