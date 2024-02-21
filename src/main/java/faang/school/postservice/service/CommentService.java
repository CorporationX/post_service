package faang.school.postservice.service;

import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.dto.kafka.NewCommentEvent;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final RedisPostRepository redisPostRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final CommentMapper commentMapper;
    private final RedisCommentMapper redisCommentMapper;

    @Value("${comment.batch.size}")
    private int commentSize;

    @Transactional
    public CommentDto createComment(long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.save(comment);
        commentEventPublisher.publish(commentDto);
        NewCommentEvent kafkaCommentEvent = NewCommentEvent.builder()
                .postId(postId)
                .authorId(commentDto.getAuthorId())
                .comment(comment)
                .build();
        kafkaCommentProducer.publishCommentEvent(kafkaCommentEvent);
        log.info("Comment created with id: {} to post: {}", commentDto.getId(), postId);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto getComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found with id: " + commentId));
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new DataNotFoundException("Comment not found with id: " + commentDto.getId()));
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        kafkaCommentProducer.publishCommentEvent(NewCommentEvent.builder()
                .postId(commentDto.getPostId())
                .authorId(commentDto.getAuthorId())
                .comment(comment)
                .build());
        return commentMapper.toDto(comment);
    }

    @Transactional
    public Page<CommentDto> getAllCommentsById(Pageable pageable, long postId) {
        return commentRepository.findAll(Example.of(Comment.builder()
                        .post(Post.builder()
                                .id(postId)
                                .build())
                        .build()), pageable)
                .map(commentMapper::toDto);
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
        kafkaCommentProducer.publishCommentEvent(NewCommentEvent.builder()
                .postId(commentId)
                .authorId(commentId)
                .comment(null)
                .build());
    }

    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    public void changeListComments(Comment comment, long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        redisPost.getComments().add(redisCommentMapper.toRedisCommentDto(comment));
        if (redisPost.getComments().size() > commentSize) {
            redisPost.getComments().remove();
        }
    }
}
