package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;


    public CommentDto createComment(long postId, CommentDto commentDto) {
        commentValidator.validationCreateComment(postId, commentDto);
        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(long postId, CommentDto commentDto) {
        commentValidator.validationUpdateComment(postId, commentDto);
        Comment comment = commentMapper.toComment(commentDto);
        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }

    public List<CommentDto> getAllComment(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
