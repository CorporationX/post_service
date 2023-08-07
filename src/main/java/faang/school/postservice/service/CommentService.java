package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final PostService postService;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validateExistingUser(commentDto);
        validateExistingPost(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().compareTo(comment2.getCreatedAt()))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public boolean deleteComment(long commentId) {
        validateExistingComment(commentId);
        commentRepository.deleteById(commentId);
        return true;
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with id: " + commentDto.getId() + " not found"));
        validateToUpdateComment(commentDto, comment);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));

    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void validateExistingUser(CommentDto commentDto) {
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null || userDto.getId() == null) {
            throw new NotFoundException("Author with id: " + commentDto.getAuthorId() + " not found!");
        }
    }

    @Retryable(retryFor = {FeignException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void validateExistingPost(CommentDto commentDto) {
        Post post = postService.getPostById(commentDto.getPostId());
        if (post == null || post.getId() < 1) {
            throw new NotFoundException("Post with id: " + commentDto.getAuthorId() + " not found!");
        }
    }

    private void validateExistingComment(long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id: " + commentId + " not found");
        }
    }

    private void validateToUpdateComment(CommentDto commentDto, Comment comment) {
        if (comment.getAuthorId() != commentDto.getAuthorId() ||
                comment.getPost().getId() != commentDto.getPostId()) {
            throw new DataValidException("Comment with id: " + commentDto.getId() + " not valid");
        }
    }
}