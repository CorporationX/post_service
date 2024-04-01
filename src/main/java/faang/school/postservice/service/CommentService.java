package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.dto.kafka.KafkaPostCommentEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.producer.KafkaPostCommentProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final UserServiceClient userServiceClient;
    private final KafkaPostCommentProducer commentProducer;

    public CommentDto addNewComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There are no posts with that id: " + postId));
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());
        Comment savedComment = commentRepository.save(comment);
        commentEventPublisher.publish(CommentEventDto.builder()
                .authorId(comment.getAuthorId())
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorPostId(post.getAuthorId())
                .postId(post.getId())
                .build());
        cacheCommentAuthor(savedComment.getAuthorId());
        sendKafkaPostCommentEvent(savedComment);

        return commentMapper.toDTO(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(allByPostId);
    }

    public CommentDto getComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
               .orElseThrow(() -> new DataValidationException("There are no comments with that id: " + commentId));
        return commentMapper.toDTO(comment);
    }

    private void cacheCommentAuthor(long authorId) {
        RedisUser commentAuthor = redisUserMapper.toRedisUser(userServiceClient.getUser(authorId));
        redisUserRepository.save(commentAuthor);
    }

    private void sendKafkaPostCommentEvent(Comment comment) {
        KafkaPostCommentEvent commentEvent = KafkaPostCommentEvent.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthorId())
                .build();
        commentProducer.sendMessage(commentEvent);
    }
}