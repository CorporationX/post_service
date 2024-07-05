package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentKafkaEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final UserContext userContext;

    @Transactional
    public CommentDto createComment(long postId, CommentDto commentDto) {
        userContext.setUserId(commentDto.getAuthorId());
        UserDto userDto = userServiceClient.getUser(commentDto.getAuthorId());
        if (userDto == null) {
            throw new DataValidationException("Comment author does not exist.");
        }
        Comment commentEntity = commentMapper.toEntity(commentDto);
        CommentDto savedCommentDto = commentMapper.toDto(commentRepository.save(commentEntity));

        addToRedisAndSendEvents(userDto, savedCommentDto);
        return savedCommentDto;
    }

    @Transactional
    public CommentDto updateComment(CommentDto commentDto) {
        Long commentDtoId = commentDto.getId();
        Comment commentEntity = commentRepository.findById(commentDtoId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with Id " + commentDtoId + " not found"));
        commentEntity.setContent(commentDto.getContent());

        return commentMapper.toDto(commentRepository.save(commentEntity));
    }

    @Transactional
    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments(long postId) {
        List<Comment> commentByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(commentByPostId);
    }

    private void addToRedisAndSendEvents(UserDto userDto, CommentDto commentDto) {
        log.info("Save user with ID: {} to Redis", userDto.getId());
        redisUserRepository.save(new UserRedis(userDto.getId(), userDto.getUsername()));

        CommentKafkaEvent commentKafkaEvent = commentMapper.fromDtoToKafkaEvent(commentDto);
        log.info("Send event with Comment ID: {} to Kafka", commentDto.getPostId());
        kafkaCommentProducer.sendEvent(commentKafkaEvent);
    }
}
