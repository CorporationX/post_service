package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final KafkaCommentProducer kafkaCommentProducer;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.validateData(commentDto);
        Comment entity = commentMapper.toEntity(commentDto);
        entity.setPost(Post.builder().id(commentDto.getPostId()).build());

        Comment savedComment = commentRepository.save(entity);
        commentEventPublisher.publish(commentDto);
        CommentDto dto = commentMapper.toDto(savedComment);
        kafkaCommentProducer.sendMessage(KafkaKey.CREATE ,dto);
        log.info("Comment to post with id={} was created successfully", commentDto.getPostId());
        return dto;
    }

    @Transactional
    public CommentDto getComment(long commentId) {
        Comment comment = commentValidator.validCommentId(commentId);
        CommentDto dto = commentMapper.toDto(comment);

        log.info("Comment with id:{} was taken from DB successfully", commentId);
        return dto;
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.validateData(commentDto);
        Comment comment = commentValidator.validCommentId(commentDto.getId());
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        kafkaCommentProducer.sendMessage(KafkaKey.UPDATE, commentDto);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getAllComments(long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = commentValidator.validCommentId(commentId);
        commentRepository.deleteById(commentId);
        kafkaCommentProducer.sendMessage(KafkaKey.DELETE, commentMapper.toDto(comment));
    }

    @Transactional(readOnly = true)
    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    @Transactional
    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }
}
