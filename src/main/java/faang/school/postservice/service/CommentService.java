package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.messaging.commentevent.CommentEventPublisher;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.exceptionhandler.ErrorCommentMessage;
import faang.school.postservice.util.validator.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateExistingUserAtCommentDto(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);
        commentEventPublisher.publish(new CommentEventDto(commentDto.getPostId(), commentDto.getAuthorId(),
                commentDto.getId()));
        return commentMapper.toDto(commentRepository.save(comment));
    }

    public List<CommentDto> getCommentsByPostId(long postId) {
        return commentRepository.findAllByPostIdSortedByCreated(postId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto updateComment(long commentId, CommentDto commentDto) {
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCommentMessage.getCommentWasNotFound(commentId)));

        validator.validateUpdateComment(commentToUpdate, commentDto);
        commentDto.setContent(commentDto.getContent());

        Comment updated = commentRepository.save(commentToUpdate);
        return commentMapper.toDto(updated);
    }

    @Transactional
    public void deleteCommentById(long commentId) {
        if (!commentRepository.existsById(commentId))
            throw new NotFoundException(ErrorCommentMessage.getCommentWasNotFound(commentId));
        commentRepository.deleteById(commentId);
    }
}
