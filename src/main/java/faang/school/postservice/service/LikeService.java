package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.Utils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

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
    private final UserServiceClient userService;
    private final UserContext userContext;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper mapper;
    private final Utils utils;

    public LikeCommentResponseDto addComment(LikeCommentRequestDto likeDto) {
        validateUser(likeDto.userId());
        Comment comment = commentService.findCommentById(likeDto.commentId());
        likeRepository.findByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
                .ifPresent(like -> {
                    throw new LikeExistsException(USER_LIKED_THIS_COMMENT);
                });
        Like like = Like.builder()
                .userId(likeDto.userId())
                .comment(comment)
                .build();
        Like resultLike = likeRepository.save(like);
        return mapper.toCommentResponseDto(resultLike);
    }

    public LikeCommentResponseDto deleteComment(LikeCommentRequestDto likeDto) {
        validateUser(likeDto.userId());
        Like like = likeRepository.deleteByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
                .orElseThrow(() -> new LikeNotFoundException(
                        utils.format(COMMENT_LIKE_NOT_FOUND, likeDto.userId(), likeDto.commentId()))
                );
        return mapper.toCommentResponseDto(like);
    }

    public LikePostResponseDto addPost(LikePostRequestDto likeDto) {
        validateUser(likeDto.userId());
        Post post = postService.findPostById(likeDto.postId());
        likeRepository.findByPostIdAndUserId(likeDto.postId(), likeDto.userId())
                .ifPresent(like -> {
                    throw new LikeExistsException(USER_LIKED_THIS_POST);
                });
        Like like = Like.builder()
                .userId(likeDto.userId())
                .post(post)
                .build();
        Like resultLike = likeRepository.save(like);
        return mapper.toPostResponseDto(resultLike);
    }

    public LikePostResponseDto deletePost(LikePostRequestDto likeDto) {
        validateUser(likeDto.userId());
        Like like = likeRepository.deleteByPostIdAndUserId(likeDto.postId(), likeDto.userId())
                .orElseThrow(() -> new LikeNotFoundException(
                        utils.format(POST_LIKE_NOT_FOUND, likeDto.userId(), likeDto.postId())));
        return mapper.toPostResponseDto(like);
    }

    private void validateUser(Long userId) {
        userContext.setUserId(userId);
        try {
            UserDto user = userService.getUser(userId);
        } catch (FeignException.NotFound fe) {
            log.error("{}", fe.getMessage(), fe);
            StringBuilder errorMessage = new StringBuilder();
            fe.responseBody()
                    .ifPresentOrElse(
                            byteBuffer -> errorMessage.append(StandardCharsets.UTF_8.decode(byteBuffer)),
                            () -> errorMessage.append(utils.format(USER_NOT_FOUND, userId)));
            throw new UserNotFoundException(errorMessage.toString());
        } catch (RuntimeException e) {
            log.error("{}", e.getMessage(), e);
            throw new LikeNotFoundException(e.getMessage());
        }
    }
}
