package faang.school.postservice.service.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.KafkaLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityAlreadyLikedException;
import faang.school.postservice.exception.EntityDeletedException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserContext context;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.kafka.topic.like}")
    private String likeTopic;

    public void addToPost(long postId) {
        long currentUserId = context.getUserId();
        log.info("Start adding like to post {} by user {}", postId, currentUserId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post {} not found", postId));
        checkUserExists(currentUserId);

        checkPossibilityLikePost(currentUserId, post);
        Like like = createLike(currentUserId, post, null);

        like = likeRepository.save(like);
        log.info("Post {} successfully liked by user {}. Like id - {}", postId, currentUserId, like.getId());

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(likeTopic, createRecordData(postId, currentUserId));
        kafkaTemplate.send(producerRecord);
    }

    public void addToComment(long commentId) {
        long currentUserId = context.getUserId();
        log.info("Start adding like to comment {} by user {}", commentId, currentUserId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment " + commentId + " not found"));
        checkUserExists(currentUserId);

        checkPossibilityLikeCommentByUser(currentUserId, comment);
        Like like = createLike(currentUserId, null, comment);

        like = likeRepository.save(like);
        log.info("Comment {} successfully liked by user {}. Like id - {}", commentId, currentUserId, like.getId());

        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(likeTopic, createRecordData(commentId, currentUserId));
        kafkaTemplate.send(producerRecord);
    }

    public void deleteFromPost(long postId) {
        long currentUserId = context.getUserId();
        log.info("Start deleting like from post {} by user {}", postId, currentUserId);
        checkUserExists(currentUserId);
        likeRepository.deleteByPostIdAndUserId(postId, currentUserId);
        log.info("Like successfully deleted from post {} by user {}", postId, currentUserId);
    }

    public void deleteFromComment(long commentId) {
        long currentUserId = context.getUserId();
        checkUserExists(currentUserId);
        log.info("Start deleting like from comment {} by user {}", commentId, currentUserId);
        likeRepository.deleteByCommentIdAndUserId(commentId, currentUserId);
        log.info("Comment successfully liked from comment {} by user {}", commentId, currentUserId);
    }

    private UserDto checkUserExists(long id) {
        return userServiceClient.getUser(id);
    }

    private void checkPossibilityLikePost(long currentUserId, Post post) {
        checkPostIsNotDeleted(post);
        checkNotExistsLikedPostByUser(currentUserId, post.getId());
        checkNotExistsLikedCommentForPostByUser(currentUserId, post.getId());
    }

    private void checkPostIsNotDeleted(Post post) {
        if (post.isDeleted()) {
            throw new EntityDeletedException("Post {} already deleted", post.getId());
        }
    }

    private void checkNotExistsLikedPostByUser(long currentUserId, Long postId) {
        likeRepository.findByPostIdAndUserId(postId, currentUserId)
                .ifPresent(like -> {
                    throw new EntityAlreadyLikedException("User {} already liked post {}", currentUserId, postId);
                });
    }

    private void checkNotExistsLikedCommentForPostByUser(long currentUserId, long postId) {
        boolean existsLikedComment = likeRepository.existsByUserIdAndCommentPostId(currentUserId, postId);
        if (existsLikedComment) {
            throw new EntityAlreadyLikedException("User {} already liked comment for post {}", currentUserId, postId);
        }
    }

    private void checkPossibilityLikeCommentByUser(long currentUserId, Comment comment) {
        chackNotExistsLikedCommentByUser(currentUserId, comment.getId());
        checkNotExistsLikedPostByUser(currentUserId, comment.getPost().getId());
    }

    private void chackNotExistsLikedCommentByUser(long currentUserId, Long commentId) {
        likeRepository.findByCommentIdAndUserId(commentId, currentUserId)
                .ifPresent(like -> {
                    throw new EntityAlreadyLikedException("User {} already liked comment {}", currentUserId, commentId);
                });
    }

    private Like createLike(long currentUserId, Post post, Comment comment) {
        return Like.builder()
                .userId(currentUserId)
                .post(post)
                .comment(comment)
                .build();
    }

    private String createRecordData(long objectId, long currentUserId) {
        KafkaLikeDto kafkaLikeDto = new KafkaLikeDto();
        kafkaLikeDto.setObjectId(objectId);
        kafkaLikeDto.setUserId(currentUserId);
        ObjectMapper objectMapper = new ObjectMapper();
        String likeDtoAsString = null;
        try {
            likeDtoAsString = objectMapper.writeValueAsString(kafkaLikeDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return likeDtoAsString;
    }
}
