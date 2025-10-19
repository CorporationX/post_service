package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Service;

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

    @Override
    public CommentDto addComment(Long postId, CreateCommentDto commentDto) {
        log.info("Adding comment to post {}.", postId);
        validateString(commentDto.content(), "Comment text");
        UserDto userDto = userServiceClient.getUser(userContext.getUserId());
        Post post = validatePostExists(postId);

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);
        comment.setAuthorId(userDto.id());
        commentRepository.save(comment);
        log.info("Comment {} saved.", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto) {
        log.info("Updating comment {}.", commentId);
        validateString(commentDto.content(), "Comment text");
        Comment comment = validateCommentExists(commentId);
        if (!userId.equals(comment.getAuthorId())) {
            throw new DataValidationException("Comment update is not allowed for current user.");
        }
        commentMapper.update(commentDto, comment);
        Post post = validatePostExists(comment.getPost().getId());
        comment.setPost(post);
        comment.setUpdatedAt(commentDto.updatedAt());
        commentRepository.save(comment);
        log.info("Comment {} has been updated.", comment.getId());
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        log.info("Searching for comments under post with ID {}", postId);
        return commentRepository.findAllByPostId(postId).stream().map(commentMapper::toCommentDto).toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Attempting to remove comment {}.", commentId);
        commentRepository.deleteById(commentId);
        log.info("Comment {} has been deleted", commentId);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private Post validatePostExists(long postId) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new NullPointerException("Post with this ID does not exist.");
        }
        return postRepository.findById(postId).get();
    }

    private Comment validateCommentExists(Long commentId) {
        if (commentRepository.findById(commentId).isEmpty()) {
            throw new NullPointerException("Comment with this ID does not exist.");
        }
        return commentRepository.findById(commentId).get();
    }
}