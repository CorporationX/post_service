package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.kafka.producers.KafkaCommentProducer;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final PostRepository postRepository;
    private final CommentEventPublisher commentEventPublisher;
    private final CommentMapper commentMapper;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final UserServiceClient userServiceClient;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;

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
    public CommentDto create(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String
                        .format("Post with id:%d doesn't exist", commentDto.getPostId())));
        comment.setPost(post);

        comment = commentRepository.save(comment);

        sendRedisEvent(commentDto);
        sendKafkaEvent(comment);
        cacheCommentAuthor(comment.getAuthorId());

        return commentMapper.toDto(comment);
    }

    private void sendRedisEvent(CommentDto commentDto) {
        CommentEventDto commentEventDto = CommentEventDto.builder()
                .authorId(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .createdAt(commentDto.getCreatedAt())
                .build();
        commentEventPublisher.publish(commentEventDto);
    }

    private void sendKafkaEvent(Comment comment) {
        KafkaCommentEvent kafkaCommentEvent = KafkaCommentEvent.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .postId(comment.getPost().getId())
                .build();
        kafkaCommentProducer.sendMessage(kafkaCommentEvent);
    }

    private void cacheCommentAuthor(long authorId) {
        UserDto author = userServiceClient.getUser(authorId);
        redisUserRepository.save(redisUserMapper.toRedisEntity(author));
    }
}
