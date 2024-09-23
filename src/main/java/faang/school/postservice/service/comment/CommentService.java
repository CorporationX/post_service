package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.kafka.CommentKafkaEvent;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.publisher.kafka.comment.KafkaCommentPublisher;
import faang.school.postservice.messaging.publisher.redis.comment.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.UserClientValidation;
import jakarta.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final RedisUserRepository redisUserRepository;
    private final CommentRepository commentRepository;
    private final UserClientValidation userClientValidation;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final CommentEventPublisher commentEventPublisher;
    private final UserServiceClient userServiceClient;
    private final KafkaCommentPublisher kafkaCommentPublisher;

    public CommentDto addNewCommentInPost(CommentDto commentDto) {
        userClientValidation.checkUser(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(postMapper.toEntity(postService.getById((commentDto.getPostId()))));
        comment.setLikes(new ArrayList<>());
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            log.error(ExceptionMessages.FAILED_PERSISTENCE, e);
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }

        commentEventPublisher.publish(commentMapper.toEvent(comment));

        // отправка пользователя в редис
        UserDto userDto = userServiceClient.getUser(comment.getAuthorId());
        redisUserRepository.save(UserRedis.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .build());
        log.info("Save user with ID: {} to Redis", userDto.getId());
        // отправка коммента в кафку
        kafkaCommentPublisher.publish(commentMapper.toCommentKafkaEvent(commentDto));
        log.info("Send event with Comment ID: {} to Kafka", commentDto.getPostId());

        return commentMapper.toDto(comment);
    }

    public CommentDto updateExistingComment(CommentDto commentDto) {
        userClientValidation.checkUser(commentDto.getAuthorId());
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow(() -> {
            log.error(ExceptionMessages.COMMENT_NOT_FOUND);
            return new NoSuchElementException(ExceptionMessages.COMMENT_NOT_FOUND);
        });
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsForPost(Long postId) {
        return postService.getById(postId).getComments();
    }

    public CommentDto deleteExistingCommentInPost(CommentDto commentDto) {
        commentRepository.deleteById(commentDto.getId());
        return commentDto;
    }
}
