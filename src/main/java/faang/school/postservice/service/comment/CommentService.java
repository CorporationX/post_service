package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;

    private final PostValidator postValidator;

    private final UserServiceClient userServiceClient;

    private final CommentMapper commentMapper;

    private final CommentValidator commentValidator;


    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);

        postValidator.getPostById(postId);

        validateUserId(commentDto.getAuthorId());

        Comment comment = commentMapper.toComment(commentDto);

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + commentId + " not found"));

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentMapper.toCommentDto((commentRepository.save(comment)));
    }
    public List<CommentDto> getAllComments(Long postId) {
        commentValidator.validateListComments(postId);

        List<Comment> comments =  commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
    public void deleteComment(Long commentId) {
        commentRepository
                .findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID" + commentId));
        commentRepository.deleteById(commentId);
    }

    private void validateUserId(long userId) {
        userServiceClient.getUser(userId);
    }
}
