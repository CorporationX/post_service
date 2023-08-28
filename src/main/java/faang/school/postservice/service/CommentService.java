package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;


    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.validateUserBeforeCreate(commentDto);
        Comment comment = commentMapper.toEntity(commentDto, postId);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = checkCommentExists(commentId);
        commentValidator.validateBeforeUpdate(comment, commentDto);
        commentMapper.partialUpdate(commentDto, comment);
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public boolean existById(long commentId){
        return commentRepository.existsById(commentId);
    }

    @Transactional(readOnly = true)
    public Long getAuthorId(long postId) {
        return commentRepository
                .findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. Id: " + postId))
                .getAuthorId();
    }

    private Comment checkCommentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
    }
}
