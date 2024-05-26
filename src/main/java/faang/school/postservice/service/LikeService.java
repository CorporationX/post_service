package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidation;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final LikeValidation likeValidation;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final LikeEventPublisher likeEventPublisher;

    public LikeDto likePost(LikeDto likeDto) {
        Post checkPost = postService.existsPost(likeDto.getPostId());
        userServiceClient.getUser(likeDto.getUserId());
        likeValidation.verifyUniquenessLikePost(likeDto.getPostId(), likeDto.getUserId());
        Like like = Like.builder()
                .id(likeDto.getUserId())
                .post(checkPost)
                .build();

        LikeEvent likeEvent = buildLikeEvent(likeDto);
        LikeEvent likeEventPost = likeEvent.toBuilder()
                .authorPostId(checkPost.getAuthorId())
                .postId(checkPost.getId())
                .build();

        likeEventPublisher.publish(likeEventPost);

        return likeMapper.toDto(likeRepository.save(like));
    }

    public void deleteLikeFromPost(Long postId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByPostIdAndUserId(postId, userDto.getId());
    }

    public LikeDto likeComment(LikeDto likeDto) {
        Comment checkComment = commentService.findCommentById(likeDto.getCommentId());
        userServiceClient.getUser(likeDto.getUserId());
        likeValidation.verifyUniquenessLikeComment(likeDto.getCommentId(), likeDto.getUserId());
        Like like = Like.builder()
                .id(likeDto.getUserId())
                .comment(checkComment)
                .build();

        LikeEvent likeEvent = buildLikeEvent(likeDto);
        LikeEvent likeEventComment = likeEvent.toBuilder()
                .authorCommentId(checkComment.getAuthorId())
                .commentId(checkComment.getId())
                .build();

        likeEventPublisher.publish(likeEventComment);

        return likeMapper.toDto(likeRepository.save(like));
    }

    public void deleteLikeFromComment(Long commentId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByCommentIdAndUserId(commentId, userDto.getId());
    }

    private UserDto getUserFromUserService() {
        try {
            return userServiceClient.getUser(userContext.getUserId());
        } catch (FeignException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    public LikeEvent buildLikeEvent(LikeDto likeDto) {
        return LikeEvent.builder()
                .authorLikeId(likeDto.getUserId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}