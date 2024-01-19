package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.messaging.kafka.events.CommentEvent;
import faang.school.postservice.messaging.kafka.publishing.KafkaCommentProducer;
import faang.school.postservice.messaging.redis.publisher.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaCommentProducer commentProducer;

    public Comment findExistingComment(long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    @Transactional(readOnly = true)
    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerifiedDateBeforeAndVerifiedFalse(LocalDateTime.now());
    }

    @Transactional
    public void processCommentsBatch(List<Comment> comments) {
        for (Comment comment : comments) {
            boolean containsBannedWord = moderationDictionary.containsBannedWord(comment.getContent());
            comment.setVerified(!containsBannedWord);
            comment.setVerifiedDate(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.validateAuthorExist(commentDto);
        commentValidator.validateCommentBeforeCreate(commentDto);

        Comment comment = commentMapper.toEntity(commentDto);
        Comment savedComment = commentRepository.save(comment);

        commentEventPublisher.publishCommentEvent(savedComment);

        commentProducer.publish(CommentEvent.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .authorId(savedComment.getAuthorId())
                .postId(commentDto.getPostId())
                .build());

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        commentValidator.validateAuthorExist(commentDto);
        commentValidator.validateCommentBeforeUpdate(commentId, commentDto);

        Comment comment = findExistingComment(commentId);
        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        commentValidator.validateCommentBeforeGetCommentsByPostId(postId);
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        return commentMapper.toDtoList(comments);
    }

    @Transactional
    public CommentDto deleteComment(Long commentId) {
        Comment comment = findExistingComment(commentId);
        commentRepository.delete(comment);
        return commentMapper.toDto(comment);
    }
}
