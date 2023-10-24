package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.CommentPublisher;
import faang.school.postservice.repository.CommentRepository;

import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final PostService postService;
    private final CommentEventPublisher commentEventPublisher;

    public CommentDto createComment(CommentDto commentDto) {
        if (commentDto.getCreatedAt() == null) {
            commentDto.setCreatedAt(LocalDateTime.now());
            commentDto.setUpdatedAt(LocalDateTime.now());
        }
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.save(comment);
        commentEventPublisher.publish(comment);
        return commentDto;
    }

    public void updateComment(CommentDto commentDto) {
        Comment comment = getCommentById(commentDto.getId());
        Post post = postService.getPostById(commentDto.getPostId());

        commentValidator.validateUpdateComment(post, comment);
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setContent(commentDto.getContent());
    }

    public List<CommentDto> getAllComments(long postId) {
        return commentMapper.toDto(
                commentRepository
                        .findAllByPostId(postId)
        );
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment was not found"));
    }
}