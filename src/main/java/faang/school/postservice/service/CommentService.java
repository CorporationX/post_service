package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.NotFoundEntityException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.kafka.KafkaCommentProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentPusher;
    private final KafkaCommentProducer kafkaCommentProducer;

    @Transactional
    public void delete(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsByPostId(long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.checkPostIsExist(commentDto.getPostId());
        Comment savedComment = commentRepository.save(commentMapper.toEntity(commentDto));
        commentPusher.publish(commentMapper.toEvent(savedComment));
        CommentDto savedCommentDto = commentMapper.toDto(savedComment);
        kafkaCommentProducer.sendEvent(commentMapper.toKafkaEvent(savedCommentDto));
        return savedCommentDto;
    }

    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto) {
        Comment savedComment = commentRepository.findById(updateCommentDto.getId())
                .orElseThrow(() -> {
                    log.info("Couldn't find the saved comment into Comment Repository : updateComment");
                    throw new  EntityNotFoundException("Couldn't find saved comment in repository ID = " + updateCommentDto.getId());
                });

        savedComment.setContent(updateCommentDto.getContent());

        return commentMapper.toDto(commentRepository.save(savedComment));
    }

    @Transactional(readOnly = true)
    public Comment validationAndCommentsReceived(LikeDto likeDto) {
        commentValidator.checkComment(likeDto);

        return commentRepository.findById(likeDto.getCommentId()).orElseThrow(() ->
                new NotFoundEntityException("Not found comment by id: " + likeDto.getCommentId()));
    }
}
