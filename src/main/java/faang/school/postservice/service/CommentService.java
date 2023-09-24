package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.messaging.commentevent.CommentEventPublisher;
import faang.school.postservice.messaging.userbanevent.UserBanEventPublisher;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.exceptionhandler.ErrorCommentMessage;
import faang.school.postservice.util.validator.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final UserBanEventPublisher userBanEventPublisher;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateExistingUserAtCommentDto(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);
        CommentEventDto commentEventDto = commentMapper.toEvent(comment);
        commentEventPublisher.publish(commentEventDto);
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

    public void banUser() {
        Map<Long, Long> banList = new HashMap<>();
        List<Comment> allComments = commentRepository.findAll();
        long commentCount = 0;

        for (Comment comment : allComments) {
            Long authorId = comment.getAuthorId();
            if (!banList.containsKey(authorId)) {
                banList.put(authorId, commentCount);
            }
            if (!comment.isVerified()) {
                long increment = banList.get(authorId) + 1;
                banList.put(authorId, increment);
            }
        }

        for (Map.Entry<Long, Long> entry : banList.entrySet()) {
            if (entry.getValue() > 5) {
                userBanEventPublisher.publish(entry.getKey());
            }
        }
    }
}
