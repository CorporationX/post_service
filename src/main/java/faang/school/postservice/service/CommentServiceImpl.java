package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserContext userContext;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private static final int MAX_ALLOWED_COMMENT_TEXT_SIZE = 4096;

    @Override
    public CommentDto addComment(Long postId, CreateCommentDto commentDto) {
        log.info("Adding comment to post {}", postId);
        validateCommentLength(commentDto.content());
        validateNotNull(commentDto.createdAt(), "Creation time/date");
        long userId = userServiceClient.getUser(userContext.getUserId()).id();
        Post post = validatePostExists(postId);

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);
        comment.setAuthorId(userId);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, UpdateCommentDto commentDto) {
        log.info("Updating comment {}", commentDto.id());
        validateCommentLength(commentDto.content());
        validateNotNull(commentDto.updatedAt(), "Update time/date");
        Comment comment = validateCommentExists(commentDto.id());
        if (!userId.equals(comment.getAuthorId())) {
            log.error("User/author ID mismatch");
            throw new IllegalArgumentException("Comment update is not allowed for current user!");
        }
        if (!comment.getPost().getId().equals(commentDto.postId())) {
            log.error("Post ID mismatch");
            throw new IllegalArgumentException("Post IDs of original and updated comments do not match!");
        }
        commentMapper.update(commentDto, comment);
        Post post = validatePostExists(comment.getPost().getId());
        comment.setPost(post);
        comment.setUpdatedAt(commentDto.updatedAt());
        commentRepository.save(comment);
        log.info("Comment {} has been updated", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        log.info("Searching for comments under post with ID {}", postId);
        List<Comment> commentsByPostId = commentRepository.findAllByPostId(postId);
        if (commentsByPostId.isEmpty()) {
            throw new IllegalArgumentException("There are no comment under post with ID: " + postId);
        }
        return commentsByPostId.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto).toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Attempting to remove comment {}", commentId);
        if (commentRepository.findById(commentId).isEmpty()) {
            throw new IllegalArgumentException("Comment with this ID does not exist: " + commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Comment {} has been deleted", commentId);
    }

    private Post validatePostExists(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with this ID does not exist: " + postId));
    }

    private Comment validateCommentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment with this ID does not exist: " + commentId));
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new IllegalArgumentException(paramName + " should be present!");
        }
    }

    private void validateCommentLength(String content) {
        if (content.length() > MAX_ALLOWED_COMMENT_TEXT_SIZE || content.isBlank()) {
            throw new IllegalArgumentException("Comment length should be less than 4096 characters and cannot be empty!");
        }
    }
}