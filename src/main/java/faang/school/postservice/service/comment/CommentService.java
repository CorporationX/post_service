package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.KafkaLikeDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.dto.comment.KafkaCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotResourceOwnerException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserContext userContext;
    private final CommentMapper mapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate redisTemplate;
    private final RedisTemplate<String, CommentRedisDto> redisCommentTemplate;
    @Value("${redis.post-expire}")
    private String commentExpire;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private String commentTopic = "comments";
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public CommentDto create(CommentDto commentDto) {
        long currentUserId = userContext.getUserId();
        log.info("Start create comment for post {} by user {}", commentDto.postId(), currentUserId);
        validateAuthor(currentUserId, commentDto.authorId());
        Post post = findPostById(commentDto.postId());
//        checkUserExists(currentUserId);
        Comment comment = mapper.toComment(commentDto);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(commentTopic, createRecordData(comment.getId(), currentUserId));
        kafkaTemplate.send(producerRecord);

        try {
            String key = "authors_" + comment.getAuthorId();
            CommentRedisDto redisDto = createRedisDto(comment);
            String string = objectMapper.writeValueAsString(redisDto);
            stringRedisTemplate.opsForList().rightPushAll(key, List.of(string));
            stringRedisTemplate.expire(key, Duration.ofMillis(Long.parseLong(commentExpire)));
        } catch(Exception e) {
            log.error(e.getMessage());
            try {
                throw e;
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }

        log.info("Comment {} successfully created for post {} by user {}",
                 comment.getId(), commentDto.postId(), currentUserId);
        return mapper.toCommentDto(comment);
    }

    public CommentDto update(long commentId, CommentDto commentDto) {
        long currentUserId = userContext.getUserId();
        log.info("Start update comment {} for post {} by user {}", commentId, commentDto.postId(), currentUserId);
        validateAuthor(currentUserId, commentDto.authorId());
        Comment comment = findCommentById(commentId);
        checkUserExists(currentUserId);
        mapper.update(commentDto.content(), comment);

        comment = commentRepository.save(comment);

        log.info("Comment {} successfully updated for post {} by user {}",
                 comment.getId(), commentDto.postId(), currentUserId);
        return mapper.toCommentDto(comment);
    }

    public void delete(long commentId) {
        long currentUserId = userContext.getUserId();
        log.info("Start delete comment {} by user {}", commentId, currentUserId);
        Comment comment = findCommentById(commentId);
        validateAuthor(currentUserId, comment.getAuthorId());
        checkUserExists(currentUserId);

        commentRepository.deleteById(commentId);
        log.info("Comment {} successfully deleted by user {}", commentId, currentUserId);
    }

    public List<CommentDto> findAllByPostId(long postId) {
        log.info("Getting all comments for post {}", postId);
        List<Comment> comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        log.info("Comments for post {} successfully received", postId);

        return comments.stream()
                .map(mapper::toCommentDto)
                .toList();
    }

    private void validateAuthor(long currentUserId, long authorId) {
        if (currentUserId != authorId) {
            throw new NotResourceOwnerException("User {} is not owner", currentUserId);
        }
    }

    private void checkUserExists(long currentUserId) {
        UserDto user = userServiceClient.getUser(currentUserId);
        if (user == null) {
            throw new EntityNotFoundException("User {} not found", currentUserId);
        }
    }

    private Post findPostById(long id) {
        return postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post {} not found", id));
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Comment {} not found", id)
        );
    }

    private String createRecordData(long objectId, long currentUserId) {
        KafkaCommentDto kafkaCommentDto = new KafkaCommentDto(objectId, currentUserId);
        ObjectMapper objectMapper = new ObjectMapper();
        String commentDtoAsString = null;
        try {
            commentDtoAsString = objectMapper.writeValueAsString(kafkaCommentDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return commentDtoAsString;
    }

    private CommentRedisDto createRedisDto(Comment comment) {
        CommentRedisDto commentRedisDto = new CommentRedisDto();
        commentRedisDto.setId(comment.getId());
        commentRedisDto.setContent(comment.getContent());
        commentRedisDto.setAuthorId(comment.getAuthorId());
        commentRedisDto.setLikeCount(comment.getLikes() != null ? comment.getLikes().size() : 0);
        commentRedisDto.setPostId(comment.getPost().getId());
        return commentRedisDto;
    }
}
