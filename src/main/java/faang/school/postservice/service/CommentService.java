package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateExistingUserAtCommentDto(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return comments.stream()
                .sorted((comment1, comment2) -> comment1.getCreatedAt().compareTo(comment2.getCreatedAt()))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " was not found!"));

        validator.validateUpdateComment(commentToUpdate, commentDto);
        commentDto.setContent(commentDto.getContent());

        Comment updated = commentRepository.save(commentToUpdate);
        return commentMapper.toDto(updated);
    }

    @Transactional
    public boolean deleteCommentById(long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException("Comment with id " + commentId + "was not found!");
        commentRepository.deleteById(commentId);
        return true;
    }
}
