package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ServiceUnavailableException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.CommentValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentValidator commentValidator;

    @Override
    public CommentDto update(Long commentId, Long authorId, SaveCommentDto dto) {
        ensureUserExists(authorId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentValidator.ensureUserIsAuthor(authorId, comment.getAuthorId());
        commentMapper.update(dto, comment);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment id: {} updated", savedComment.getId());
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public void delete(Long commentId, Long userId) {
        ensureUserExists(userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        Post post = postService.getPostById(comment.getPost().getId());
        commentValidator.ensureUserCanDeleteComment(comment, post, userId);
        commentRepository.deleteById(commentId);
        log.info("Comment id: {} deleted", commentId);
    }

    private void ensureUserExists(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.warn("User with id={} not found: {}", userId, e.getMessage());
            throw new EntityNotFoundException("User not found with id: " + userId);
        } catch (FeignException e) {
            log.error("Error while checking user existence: {}", e.getMessage());
            throw new ServiceUnavailableException("User service unavailable");
        }
    }
}
