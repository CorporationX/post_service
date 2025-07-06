package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.Utils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    public static final String USER_LIKED_THIS_POST = "user liked this post";
    public static final String USER_LIKED_THIS_COMMENT = "user liked this comment";
    public static final String COMMENT_LIKE_NOT_FOUND = "like for user [{}] and comment [{}] not found";
    public static final String POST_LIKE_NOT_FOUND = "like for user [{}] and post [{}] and not found";
    public static final String USER_NOT_FOUND = "user by id={} find error";

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;
    private final LikeMapper mapper;
    private final Utils utils;
    private final ObjectMapper objectMapper;

    public LikeDto addLikeToComment(LikeDto likeDto) {
        validateUser(likeDto.userId());
        Comment comment = commentService.findCommentById(likeDto.commentId());
        likeRepository.findByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
                .ifPresent(like -> {
                    throw new LikeExistsException(USER_LIKED_THIS_COMMENT);
                });
        Like like = likeRepository.save(getLike(likeDto, comment));
        publishLikeToComment(like, likeDto.commentId());
        return mapper.toDto(like);
    }

    public void deleteLikeFromComment(LikeDto likeDto) {
        validateUser(likeDto.userId());
        likeRepository.deleteByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
                .orElseThrow(() -> new LikeNotFoundException(
                        utils.format(COMMENT_LIKE_NOT_FOUND, likeDto.userId(), likeDto.commentId())));
    }

    public LikeDto addLikeToPost(LikeDto likeDto) {
        validateUser(likeDto.userId());
        Post post = postService.findPostById(likeDto.postId());
        likeRepository.findByPostIdAndUserId(likeDto.postId(), likeDto.userId())
                .ifPresent(like -> {
                    throw new LikeExistsException(USER_LIKED_THIS_POST);
                });
        Like like = likeRepository.save(getLike(likeDto, post));
        publishLikeToPost(like, likeDto.postId());
        return mapper.toDto(like);
    }

    public void deleteLikeFromPost(LikeDto likeDto) {
        validateUser(likeDto.userId());
        likeRepository.deleteByPostIdAndUserId(likeDto.postId(), likeDto.userId())
                .orElseThrow(() -> new LikeNotFoundException(
                        utils.format(POST_LIKE_NOT_FOUND, likeDto.userId(), likeDto.postId())));
    }

    private void publishLikeToComment(Like like, Long commentId) {
        LikeEventDto likeEventDto = LikeEventDto.builder()
            .authorId(like.getComment().getAuthorId())
            .senderId(like.getUserId())
            .commentId(commentId)
            .date(LocalDateTime.now())
            .build();
        likeEventPublisher.publish(likeEventDto);
    }

    private void publishLikeToPost(Like like, Long postId) {
        LikeEventDto likeEventDto = LikeEventDto.builder()
            .authorId(like.getPost().getAuthorId())
            .senderId(like.getUserId())
            .postId(postId)
            .date(LocalDateTime.now())
            .build();
        likeEventPublisher.publish(likeEventDto);
    }

    private Like getLike(LikeDto likeDto, Comment comment) {
        return Like.builder()
                .userId(likeDto.userId())
                .comment(comment)
                .build();
    }

    private Like getLike(LikeDto likeDto, Post post) {
        return Like.builder()
                .userId(likeDto.userId())
                .post(post)
                .build();
    }

    private void validateUser(Long userId) {
        try {
            userServiceClient.checkUser(userId);
        } catch (FeignException fe) {
            log.error("FeignException.status is: [{}]", fe.status());
            log.error("validateUser: {}", fe.getMessage(), fe);
            StringBuilder resultMessage = new StringBuilder();
            if (fe.status() == -1) {
                resultMessage.append(utils.format(USER_NOT_FOUND, userId));
            } else {
                String feignExceptionMessage = fe.contentUTF8();
                if (feignExceptionMessage != null) {
                    try {
                        ErrorResponse errorResponse = objectMapper.readValue(
                                feignExceptionMessage, ErrorResponse.class);
                        resultMessage.append(errorResponse.getErrorMessage());
                    } catch (JsonProcessingException e) {
                        log.error("validateUser.JsonProcessingException: {}", e.getMessage(), e);
                        resultMessage.append(utils.format(USER_NOT_FOUND, userId));
                    }
                } else {
                    resultMessage.append(utils.format(USER_NOT_FOUND, userId));
                }
            }
            throw new UserNotFoundException(resultMessage.toString());
        }
    }
}
