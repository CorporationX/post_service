package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.comment.CommentValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.CommentValidator;
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

    @Transactional
    public Comment create(long postId, Comment comment) {
        commentValidator.validateCommentAuthor(comment.getAuthorId());

        Post post = postService.getPostById(postId);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        log.debug("Создан комментарий с id={}", comment.getId());
        return savedComment;
    }

    @Transactional
    public Comment update(Comment comment) {
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
            throw new CommentValidationException(String.format("Комментарий с id=%d не найден и не может быть удален", commentId));
        }

        commentRepository.deleteById(commentId);
        log.debug("Комментарий с id={} успешно удален", commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerified();
    }

    @Transactional
    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }
}
