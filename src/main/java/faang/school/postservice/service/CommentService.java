package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Setter
@Slf4j
public class CommentService {

    private final PostService postService;
    private final RedisCacheService redisCacheService;
    private final CommentRepository commentRepository;
    private final CommentEventPublisher redisCommentEventPublisher;
    private final KafkaCommentProducer kafkaCommentEventPublisher;
    private final CommentMapper commentMapper;
    private final RedisCommentMapper redisCommentMapper;
    private final CommentValidator commentValidator;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.redis.util.comments-amount}")
    private int maxAmountOfComments;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        long postId = commentDto.getPostId();
        long authorId = commentDto.getAuthorId();

        commentValidator.validateUserExistence(authorId);

        Post post = postService.findAlredyPublishedAndNotDeletedPost(postId);

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        Comment entity = commentRepository.save(comment);
        log.info("Comment saved successfully.It's ID: {}", entity.getId());

        publishCommentCreationEvent(entity);
        publishKafkaCommentEvent(entity);

        redisCacheService.findOrCacheRedisUser(authorId);
        return commentMapper.toDto(comment);
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Comment with id:%d doesn't exist", commentId)));
    }

    public List<Comment> findUnverifiedComments() {
        return commentRepository.findUnverifiedComments();
    }

    public void saveAll(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Async("commentTaskExecutor")
    public void addCommentToPost(long postId, RedisCommentDto commentDto) {
        Optional<RedisPost> optionalRedisPost = redisCacheService.findByRedisPostBy(postId);

        if (optionalRedisPost.isPresent()) {
            log.info("Post with ID {} were found in Redis", postId);

            RedisPost post = optionalRedisPost.get();
            List<RedisCommentDto> comments = post.getCommentsDto();

            if (comments == null) {
                log.warn("List of comments in Post with ID {} is empty. Creating a list and adding a single comment.", post);

                comments = new ArrayList<>(maxAmountOfComments);
                comments.add(commentDto);
            }
            int amountOfComments = comments.size();

            if (amountOfComments >= maxAmountOfComments) {
                log.warn("There are too many comments in the Post with ID {}. Deleting the last one.", postId);

                comments.subList(maxAmountOfComments - 1, amountOfComments).clear();
            }
            comments.add(0, commentDto);
            post.setCommentsDto(comments);
            post.incrementPostVersion();

            redisCacheService.updateRedisPost(postId, post);
            log.info("Comment with ID: {}, has been successfully added to Post with ID: {}. Amount of comments {}", commentDto.getId(), postId, comments.size());
        } else {
            log.warn("Post with ID {} was not found in Redis. Attempting to retrieve it from the Database.", postId);
            RedisPost post = postService.mapPostToRedisPost(postService.findPostBy(postId));

            redisCacheService.saveRedisPost(post);
        }
    }

    private void publishCommentCreationEvent(Comment comment) {
        long commentId = comment.getId();
        long authorId = comment.getAuthorId();
        long postId = comment.getPost().getId();

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentId(commentId)
                .authorId(authorId)
                .postId(postId)
                .createdAt(comment.getCreatedAt())
                .build();
        redisCommentEventPublisher.publish(commentEventDto);
        log.info("Comment creation event were published with comment ID: {}, author ID: {}, post ID: {}", commentId, authorId, postId);
    }

    private void publishKafkaCommentEvent(Comment comment) {
        long postId = comment.getPost().getId();

        KafkaCommentEvent event = KafkaCommentEvent.builder()
                .postId(postId)
                .commentDto(redisCommentMapper.toDto(comment))
                .build();
        kafkaCommentEventPublisher.publish(event);
        log.info("Comment event with Post ID: {}, and Author ID: {}, has been successfully published", postId, comment.getAuthorId());
    }
}
