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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserContext userContext;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto addComment(Long postId, CreateCommentDto commentDto) {
        log.info("Adding comment to post {}", postId);
        long userId = userServiceClient.getUser(userContext.getUserId()).id();
        Post post = validatePostExists(postId);

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);
        comment.setAuthorId(userId);
        commentRepository.save(comment);
        log.info("Comment {} saved", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, UpdateCommentDto commentDto) {
        log.info("Updating comment {}", commentDto.id());

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
}