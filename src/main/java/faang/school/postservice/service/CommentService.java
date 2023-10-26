package faang.school.postservice.service;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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

    private final static int MAX_COMMENTS = 3;

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Comment with id:%d doesn't exist", commentId)));
    }

    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Transactional
    public RedisCommentDto createComment(long postId, RedisCommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.save(comment);
        KafkaCommentEvent kafkaCommentEvent = KafkaCommentEvent.builder()
                .postId(postId)
                .authorId(commentDto.getAuthorId())
                .comment(comment)
                .build();
        kafkaCommentProducer.publishCommentEvent(kafkaCommentEvent);
        return commentMapper.toDto(comment);
    }
    @Transactional
    public CommentEventDto create() {
        CommentEventDto commentEventDto =CommentEventDto.builder()
                .authorId(new Random().nextLong(100))
                .postId(new Random().nextLong(10))
                .createdAt(LocalDateTime.now())
                .build();
        commentEventPublisher.publish(commentEventDto);
        return commentEventDto;
    }

    public void changeListComments(Comment content, long postId) {
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        redisPost.getComments().add(content);
        if (redisPost.getComments().size() > MAX_COMMENTS) {
            redisPost.getComments().remove();
        }
    }
}
