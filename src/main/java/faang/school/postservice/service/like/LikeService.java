package faang.school.postservice.service.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.publisher.MessageSenderForLikeAnalyticsImpl;
import faang.school.postservice.dto.like.AnalyticsEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeDtoForComment;
import faang.school.postservice.dto.like.LikeDtoForPost;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.kafka.KafkaService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final UserDtoValidator userDtoValidator;
    private final MessageSenderForLikeAnalyticsImpl likeEventPublisher;
    private final ObjectMapper objectMapper;
    private final KafkaService kafkaService;

    @Transactional
    public ResponseLikeDto addLikeByPost(LikeDtoForPost likeDtoForPost) {
        userDtoExists(likeDtoForPost);
        Post post = postService.findPostById(likeDtoForPost.getPostId());
        Long userId = likeDtoForPost.getUserId();

        likeRepository.findByPostIdAndUserId(likeDtoForPost.getPostId(),
                userId).ifPresent(like -> {
            throw new IllegalArgumentException("User has already liked this post");
        });

        Like likeForPost = Like
                .builder()
                .userId(userId)
                .post(post)
                .build();
        likeRepository.save(likeForPost);

        publishLikeEvent(userId, post.getAuthorId());
        kafkaService.sendLikeEvent(post.getId(), userId);
        return likeMapper.toLikeDtoFromEntity(likeForPost);
    }

    private void publishLikeEvent(Long userId, Long postAuthorId) {
        AnalyticsEventDto likeAnalyticsDto = AnalyticsEventDto
                .builder()
                .actorId(userId)
                .receiverId(postAuthorId)
                .receivedAt(LocalDateTime.now())
                .build();
        try {
            likeEventPublisher.send(objectMapper.writeValueAsString(likeAnalyticsDto));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot process message json for Like event");
        }
    }

    @Transactional
    public void deleteLikeFromPost(LikeDtoForPost likeDtoForPost) {
        if (!postService.existsPost(likeDtoForPost.getPostId())) {
            throw new EntityNotFoundException("Post not found");
        }
        likeRepository.deleteByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId());
    }

    @Transactional
    public ResponseLikeDto addLikeByComment(LikeDtoForComment likeDtoForComment) {
        userDtoExists(likeDtoForComment);
        Comment comment = commentService.findCommentById(likeDtoForComment.getCommentId());

        likeRepository.findByCommentIdAndUserId(likeDtoForComment.getCommentId(), likeDtoForComment.getUserId())
                .ifPresent(like -> {
                    throw new IllegalArgumentException("User has already liked this comment");
                });

        Like likeForComment = Like
                .builder()
                .userId(likeDtoForComment.getUserId())
                .comment(comment)
                .build();

        likeRepository.save(likeForComment);
        return likeMapper.toLikeDtoFromEntity(likeForComment);
    }

    @Transactional
    public void deleteLikeFromComment(LikeDtoForComment likeDtoForComment) {
        if (!commentService.isExits(likeDtoForComment.getCommentId())) {
            throw new IllegalArgumentException("Comment not found");
        }
        likeRepository.deleteByCommentIdAndUserId(likeDtoForComment.getCommentId(), likeDtoForComment.getUserId());
    }

    private <T extends LikeDto> void userDtoExists(T verifiedDto) {
        long userId = verifiedDto.getUserId();
        UserDto userDto = userServiceClient.getUser(userId);
        userDtoValidator.validateUserDto(userDto);
    }
}
