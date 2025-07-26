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

import java.util.List;

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
    public CommentDto create(Long postId, Long authorId, SaveCommentDto saveCommentDto) {
        ensureUserExists(authorId);
        Post post = postService.getPostById(postId);
        Comment comment = commentMapper.toComment(saveCommentDto);
        comment.setAuthorId(authorId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment id: {} for post id: {} created", savedComment.getId(), postId);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public CommentDto update(Long postId, Long commentId, Long authorId, SaveCommentDto dto) {
        ensureUserExists(authorId);
        boolean postExists = postService.existsById(postId);
        commentValidator.ensurePostExists(postExists, postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentValidator.ensureCommentBelongsToPost(comment, postId);
        commentValidator.ensureUserIsAuthor(authorId, comment.getAuthorId());
        commentMapper.update(dto, comment);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment id: {} for post id: {} updated", savedComment.getId(), postId);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getByPostId(Long postId) {
        boolean postExists = postService.existsById(postId);
        commentValidator.ensurePostExists(postExists, postId);
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        log.info("Retrieved {} comments for postId: {}", comments.size(), postId);
        return commentMapper.toCommentDtos(comments);
    }

    @Override
    public void delete(Long postId, Long commentId, Long userId) {
        ensureUserExists(userId);
        Post post = postService.getPostById(postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        commentValidator.ensureCommentBelongsToPost(comment, postId);
        commentValidator.ensureUserCanDeleteComment(comment, post, userId);
        commentRepository.deleteById(commentId);
        log.info("Comment id: {} for post id: {} deleted", commentId, postId);
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
