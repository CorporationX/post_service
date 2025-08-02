package faang.school.postservice.service;

import faang.school.postservice.client.FeignClientValidator;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.EventProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    public static final String USER_LIKED_THIS_POST = "user liked this post";
    public static final String USER_LIKED_THIS_COMMENT = "user liked this comment";
    public static final String COMMENT_LIKE_NOT_FOUND = "like for user [{}] and comment [{}] not found";
    public static final String USER_NOT_FOUND = "user by id={} find error";
    public static final String POST_LIKE_NOT_FOUND = "like for user [{}] and post [{}] and not found";

    private final LikeRepository likeRepository;
    private final FeignClientValidator feignClientValidator;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final EventProducer<LikeEventDto> likeEventProducer;
    private final LikeMapper likeMapper;
    private final Utils utils;

    @Transactional
    public LikeDto addLikeToComment(LikeDto likeDto) {
        validateUser(likeDto.userId());
        Comment comment = commentService.findCommentById(likeDto.commentId());
        likeRepository.findByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
            .ifPresent(like -> {
                throw new LikeExistsException(USER_LIKED_THIS_COMMENT);
            });
        Like like = likeRepository.save(getLike(likeDto, comment));
        publishLikeToComment(like, likeDto.commentId());
        return likeMapper.toDto(like);
    }


    @Transactional
    public void deleteLikeFromComment(LikeDto likeDto) {
        validateUser(likeDto.userId());
        likeRepository.deleteByCommentIdAndUserId(likeDto.commentId(), likeDto.userId())
            .orElseThrow(() -> new LikeNotFoundException(
                utils.format(COMMENT_LIKE_NOT_FOUND, likeDto.userId(), likeDto.commentId())));
    }

    @Transactional
    public LikeDto addLikeToPost(LikeDto likeDto) {
        validateUser(likeDto.userId());
        Post post = postService.findPostById(likeDto.postId());
        likeRepository.findByPostIdAndUserId(likeDto.postId(), likeDto.userId())
            .ifPresent(like -> {
                throw new LikeExistsException(USER_LIKED_THIS_POST);
            });
        Like like = likeRepository.save(getLike(likeDto, post));
        publishLikeToPost(like, likeDto.postId());
        return likeMapper.toDto(like);
    }

    @Transactional
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
        likeEventProducer.publish(likeEventDto);
    }

    private void publishLikeToPost(Like like, Long postId) {
        LikeEventDto likeEventDto = LikeEventDto.builder()
            .authorId(like.getPost().getAuthorId())
            .senderId(like.getUserId())
            .postId(postId)
            .date(LocalDateTime.now())
            .build();
        likeEventProducer.publish(likeEventDto);
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

    private void validateUser(long userId) {
        feignClientValidator.validateById(
            () -> userServiceClient.checkUser(userId),
            utils.format(USER_NOT_FOUND, userId));
    }
}
